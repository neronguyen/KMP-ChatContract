package io.github.neronguyenvn.nerochat.contract

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertTrue

class KmpPurityBytecodeTest {

    private val modules = listOf(
        "contract/common",
        "contract/auth",
        "contract/chat",
        "validation/common",
        "validation/auth",
        "validation/chat",
    )

    private val allowedJavaClassPrefixes = listOf(
        "java/lang/invoke/",
    )

    private val allowedJavaClasses = setOf(
        "java/lang/Object",
        "java/lang/String",
        "java/lang/CharSequence",
        "java/lang/Comparable",
        "java/lang/Iterable",
        "java/lang/Enum",
        "java/lang/Throwable",
        "java/lang/RuntimeException",
        "java/lang/IllegalArgumentException",
        "java/lang/IllegalStateException",
        "java/lang/Number",
        "java/lang/Integer",
        "java/lang/Long",
        "java/lang/Boolean",
        "java/lang/Double",
        "java/lang/Float",
        "java/lang/Short",
        "java/lang/Byte",
        "java/lang/Character",
        "java/lang/Void",
        "java/lang/Class",
        "java/lang/StringBuilder",
        "java/lang/Deprecated",
        "java/lang/annotation/Annotation",
        "java/lang/annotation/Retention",
        "java/lang/annotation/RetentionPolicy",
        "java/lang/annotation/Target",
        "java/lang/annotation/ElementType",
        "java/util/List",
        "java/util/Map",
        "java/util/Set",
        "java/util/Collection",
        "java/util/Iterator",
        "java/util/ArrayList",
        "java/util/LinkedHashMap",
        "java/util/Arrays",
        "java/util/Objects",
        "java/util/Locale",
    )

    private val bannedMemberRefs = setOf(
        "java/lang/String.codePointCount",
        "java/lang/String.format",
        "java/lang/String.formatted",
    )

    private val bannedClassPrefixes = listOf(
        "java/util/UUID",
        "java/time/",
        "java/io/",
        "java/nio/",
        "java/net/",
    )

    @Test
    fun `src main kotlin files contain no java or javax imports`() {
        val rootDir = findRepoRoot()
        val violations = mutableListOf<String>()

        for (module in modules) {
            val mainSrcDir = File(rootDir, "$module/src/main")
            if (!mainSrcDir.exists()) continue

            mainSrcDir.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { file ->
                    file.readLines().forEachIndexed { idx, line ->
                        val trimmed = line.trim()
                        if (trimmed.startsWith("import java.") || trimmed.startsWith("import javax.")) {
                            val relPath = file.relativeTo(rootDir).path
                            violations += "$relPath:${idx + 1}: $trimmed"
                        }
                    }
                }
        }

        assertTrue(
            violations.isEmpty(),
            "Forbidden java.*/javax.* imports in src/main:\n${violations.joinToString("\n")}",
        )
    }

    @Test
    fun `compiled main classfiles contain no forbidden java bytecode references`() {
        val rootDir = findRepoRoot()
        val violations = mutableListOf<String>()
        var inspectedClasses = 0

        for (module in modules) {
            val classesDir = File(rootDir, "$module/build/classes/kotlin/main")
            assertTrue(
                classesDir.exists(),
                "Expected compiled main classes directory for $module at ${classesDir.absolutePath}",
            )

            classesDir.walkTopDown()
                .filter { it.isFile && it.extension == "class" }
                .forEach { classFile ->
                    inspectedClasses++
                    val relPath = classFile.relativeTo(rootDir).path
                    val refs = parseConstantPoolRefs(classFile.readBytes())

                    for (cls in refs.classes) {
                        if (!cls.startsWith("java/") && !cls.startsWith("javax/")) continue
                        if (bannedClassPrefixes.any { cls.startsWith(it) } ||
                            (cls !in allowedJavaClasses && allowedJavaClassPrefixes.none { cls.startsWith(it) })
                        ) {
                            violations += "$relPath references forbidden class $cls"
                        }
                    }

                    for ((owner, member) in refs.members) {
                        val fullMember = "$owner.$member"
                        if (fullMember in bannedMemberRefs) {
                            violations += "$relPath invokes banned member $fullMember"
                        }
                    }
                }
        }

        assertTrue(inspectedClasses > 0, "Expected at least one compiled .class file to inspect")
        assertTrue(
            violations.isEmpty(),
            "Forbidden JVM-only bytecode references found:\n${violations.joinToString("\n")}",
        )
    }

    private fun findRepoRoot(): File {
        var dir: File? = File(".").absoluteFile.canonicalFile
        while (dir != null) {
            if (File(dir, "settings.gradle.kts").exists()) return dir
            dir = dir.parentFile
        }
        error("Could not locate repository root containing settings.gradle.kts")
    }

    private data class ConstantPoolRefs(
        val classes: Set<String>,
        val members: Set<Pair<String, String>>,
    )

    private fun parseConstantPoolRefs(bytes: ByteArray): ConstantPoolRefs {
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val magic = buf.int
        require(magic == 0xCAFEBABE.toInt()) { "Invalid class file magic" }
        buf.short // minor
        buf.short // major
        val cpCount = buf.short.toInt() and 0xFFFF

        val utf8 = arrayOfNulls<String>(cpCount)
        val classNameIdx = IntArray(cpCount)
        val nameAndTypeNameIdx = IntArray(cpCount)
        val memberClassIdx = IntArray(cpCount)
        val memberNatIdx = IntArray(cpCount)
        val isMemberRef = BooleanArray(cpCount)

        var i = 1
        while (i < cpCount) {
            when (val tag = buf.get().toInt() and 0xFF) {
                1 -> { // CONSTANT_Utf8
                    val len = buf.short.toInt() and 0xFFFF
                    val strBytes = ByteArray(len)
                    buf.get(strBytes)
                    utf8[i] = strBytes.decodeToString()
                }
                3, 4 -> buf.position(buf.position() + 4) // Integer, Float
                5, 6 -> { // Long, Double (occupies 2 slots)
                    buf.position(buf.position() + 8)
                    i++
                }
                7 -> classNameIdx[i] = buf.short.toInt() and 0xFFFF // CONSTANT_Class
                8, 16, 19, 20 -> buf.position(buf.position() + 2) // String, MethodType, Module, Package
                9, 10, 11 -> { // Fieldref, Methodref, InterfaceMethodref
                    memberClassIdx[i] = buf.short.toInt() and 0xFFFF
                    memberNatIdx[i] = buf.short.toInt() and 0xFFFF
                    isMemberRef[i] = true
                }
                12 -> { // NameAndType
                    nameAndTypeNameIdx[i] = buf.short.toInt() and 0xFFFF
                    buf.short // descriptor_index
                }
                15 -> buf.position(buf.position() + 3) // MethodHandle
                17, 18 -> buf.position(buf.position() + 4) // Dynamic, InvokeDynamic
                else -> error("Unknown constant pool tag $tag at index $i")
            }
            i++
        }

        val classes = linkedSetOf<String>()
        for (idx in 1 until cpCount) {
            val nameIdx = classNameIdx[idx]
            if (nameIdx != 0) {
                val raw = utf8[nameIdx] ?: continue
                // Skip array descriptors like "[Ljava/lang/Object;" by extracting element type if object array
                val normalized = if (raw.startsWith("[")) {
                    val lIdx = raw.indexOf('L')
                    if (lIdx >= 0 && raw.endsWith(";")) raw.substring(lIdx + 1, raw.length - 1) else continue
                } else {
                    raw
                }
                classes += normalized
            }
        }

        val members = linkedSetOf<Pair<String, String>>()
        for (idx in 1 until cpCount) {
            if (!isMemberRef[idx]) continue
            val cIdx = memberClassIdx[idx]
            val natIdx = memberNatIdx[idx]
            val owner = utf8[classNameIdx[cIdx]] ?: continue
            val memberName = utf8[nameAndTypeNameIdx[natIdx]] ?: continue
            members += owner to memberName
        }

        return ConstantPoolRefs(classes = classes, members = members)
    }
}
