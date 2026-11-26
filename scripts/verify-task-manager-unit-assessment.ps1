param(
    [string]$JavaHome,
    [string]$MockitoAgentPath,
    [switch]$Offline
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$repoRoot = Split-Path $PSScriptRoot -Parent
$zipPath = Join-Path $repoRoot 'public/downloads/task-manager-starter.zip'
$assessmentRoot = Join-Path $repoRoot 'course-materials/software-engineering-ai/semester/unit-test-assessment'
$hintText = [IO.File]::ReadAllText((Join-Path $assessmentRoot 'hint.md'))
$expectedClasses = @{
    TaskManagerVariantATest = 10
    TaskManagerVariantBTest = 10
    TaskManagerModelCompatibilityTest = 3
}

# Inspect the exact public archive before adding private verification code.
$zip = [IO.Compression.ZipFile]::OpenRead($zipPath)
try {
    foreach ($entry in $zip.Entries) {
        if ($entry.FullName -match '(^|/)(hint\.md|\.env|target|node_modules)(/|$)|TaskManagerVariant[AB]Test|TaskManagerModelCompatibilityTest|\.class$') {
            throw "Private or generated content in student ZIP: $($entry.FullName)"
        }
    }
} finally { $zip.Dispose() }

$checkRoot = Join-Path $repoRoot ('.migration/task-manager-assessment-' + [guid]::NewGuid().ToString('N').Substring(0, 8))
[IO.Compression.ZipFile]::ExtractToDirectory($zipPath, $checkRoot)
$projectRoot = Join-Path $checkRoot 'task-manager'
$testRoot = Join-Path $projectRoot 'src/test/java/bg/tu_varna/sit/task_manager'
$foundClasses = @{}
foreach ($block in [regex]::Matches($hintText, '(?s)```java\s+(.*?)```')) {
    $source = $block.Groups[1].Value.Trim() + "`n"
    $classMatch = [regex]::Match($source, '(?m)^class (TaskManager\w+Test)\s*\{')
    if (-not $classMatch.Success) { throw 'Unrecognized Java solution block in hint.md' }
    $className = $classMatch.Groups[1].Value
    if (-not $expectedClasses.ContainsKey($className) -or $foundClasses.ContainsKey($className)) {
        throw "Unexpected or duplicated solution class: $className"
    }
    $count = [regex]::Matches($source, '(?m)^\s*@Test\s*$').Count
    if ($count -ne $expectedClasses[$className]) { throw "Wrong test count in $className : $count" }
    if ($source -match 'ReflectionTestUtils|setField\(|@SpringBootTest|MockMvc') {
        throw "Forbidden dependency in unit solution: $className"
    }
    if ($className -match 'Variant([AB])Test$') {
        $variant = $Matches[1].ToLowerInvariant()
        $assignment = [IO.File]::ReadAllText((Join-Path $assessmentRoot "variant-$variant.md"))
        $methods = [regex]::Matches($source, '(?m)^\s*void ([ab]\d{2}_\w+)\(')
        if ($methods.Count -ne 10) { throw "Expected ten named test cases in $className" }
        foreach ($method in $methods) {
            if (-not $assignment.Contains($method.Groups[1].Value)) {
                throw "Solution method missing from assignment: $($method.Groups[1].Value)"
            }
        }
    }
    [IO.File]::WriteAllText((Join-Path $testRoot "$className.java"), $source, [Text.UTF8Encoding]::new($false))
    $foundClasses[$className] = $true
}
if ($foundClasses.Count -ne 3) { throw 'Expected two solutions and one compatibility test class' }

$mavenArgs = @('test', '-Dtest=TaskManagerVariantATest,TaskManagerVariantBTest,TaskManagerModelCompatibilityTest,TaskManagerBaselineTest,DateRangeValidatorTest')
if ($Offline) { $mavenArgs += '-o' }
if ($MockitoAgentPath) {
    $agentPath = (Resolve-Path -LiteralPath $MockitoAgentPath).Path.Replace('\', '/')
    $mavenArgs += ('-DargLine=-javaagent:"' + $agentPath + '"')
}
if ($JavaHome) {
    $env:JAVA_HOME = (Resolve-Path -LiteralPath $JavaHome).Path
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
}

Push-Location $projectRoot
try {
    & .\mvnw.cmd @mavenArgs
    if ($LASTEXITCODE -ne 0) { throw "Maven failed; see $projectRoot/target/surefire-reports" }
} finally { Pop-Location }

$testCount = 0
foreach ($report in Get-ChildItem -LiteralPath (Join-Path $projectRoot 'target/surefire-reports') -Filter 'TEST-*.xml') {
    [xml]$results = Get-Content -LiteralPath $report.FullName -Raw -Encoding utf8
    if ([int]$results.testsuite.failures -ne 0 -or [int]$results.testsuite.errors -ne 0 -or [int]$results.testsuite.skipped -ne 0) {
        throw "Unsuccessful tests in $($report.Name)"
    }
    $testCount += [int]$results.testsuite.tests
}
if ($testCount -ne 30) { throw "Expected 30 successful test executions, got $testCount" }
Write-Output "PASS: 10 tests per variant, 3 compatibility checks and 7 original tests."
Write-Output "Verified extracted archive in $checkRoot"
