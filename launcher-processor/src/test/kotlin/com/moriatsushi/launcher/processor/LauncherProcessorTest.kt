package com.moriatsushi.launcher.processor

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LauncherProcessorTest {
    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `successfully generated`() {
        val kotlinSource = SourceFile.kotlin(
            "Test.kt",
            """
                package testPackage

                import com.moriatsushi.launcher.Entry

                @Entry
                fun Main() {
                }
            """,
        )
        val complication = createCompilation(kotlinSource)
        val result = complication.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.OK)

        val generatedFiles = findGeneratedFiles(complication)
        assertThat(generatedFiles).hasSize(1)

        val generatedFile = generatedFiles.first()
        assertThat(generatedFile.name).isEqualTo("ComposeActivity.kt")

        val generatedString = generatedFile.readText()
        assertThat(generatedString).contains("ComposeActivity")
        assertThat(generatedString).contains("testPackage.Main()")
    }

    @Test
    fun `Do not generate if there is no target`() {
        val kotlinSource = SourceFile.kotlin(
            "Test.kt",
            """
                package testPackage

                fun Main() {
                }
            """,
        )
        val complication = createCompilation(kotlinSource)
        val result = complication.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.OK)

        val generatedFiles = findGeneratedFiles(complication)
        assertThat(generatedFiles).isEmpty()
    }

    private fun createCompilation(vararg sourceFiles: SourceFile): KotlinCompilation {
        return KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            sources = sourceFiles.asList()
            inheritClassPath = true
            symbolProcessorProviders = listOf(LauncherProcessorProvider())
            kspIncremental = true
            inheritClassPath = true
        }
    }

    private fun findGeneratedFiles(compilation: KotlinCompilation): List<File> {
        return compilation.kspSourcesDir
            .walkTopDown()
            .filter { it.isFile }
            .toList()
    }
}
