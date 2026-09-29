# Rebuild the public starter from an explicit list of course source files.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$repoRoot = Split-Path $PSScriptRoot -Parent
$materialRoot = Join-Path $repoRoot 'course-materials/applied-web-security'
$projectRoot = Join-Path $materialRoot 'task-manager'
$outputDirectory = Join-Path $repoRoot 'public/downloads'
$outputPath = Join-Path $outputDirectory 'task-manager-starter.zip'
$entries = @{}
$rootFiles = @(
    '.dockerignore', '.gitattributes', '.gitignore', 'pom.xml', 'mvnw', 'mvnw.cmd',
    'Dockerfile', 'compose.yml', 'init-environment.ps1',
    'init-environment.sh', 'README.md', 'SOURCE.md'
)
foreach ($name in $rootFiles) {
    $entries["task-manager/$name"] = Join-Path $projectRoot $name
}
foreach ($directory in @('src/main', '.mvn')) {
    Get-ChildItem -LiteralPath (Join-Path $projectRoot $directory) -File -Recurse -Force |
        Where-Object { $_.Extension -in @('.java', '.properties') } |
        ForEach-Object {
            $relative = $_.FullName.Substring($projectRoot.Length + 1).Replace('\', '/')
            $entries["task-manager/$relative"] = $_.FullName
        }
}
foreach ($name in @('setup.md', 'architecture/system-overview.md')) {
    $entries[$name] = Join-Path $materialRoot $name
}
New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
$stream = [System.IO.File]::Open($outputPath, [System.IO.FileMode]::Create)
$archive = [System.IO.Compression.ZipArchive]::new($stream, [System.IO.Compression.ZipArchiveMode]::Create)
try {
    foreach ($name in ($entries.Keys | Sort-Object)) {
        $entry = $archive.CreateEntry($name, [System.IO.Compression.CompressionLevel]::Optimal)
        $entry.LastWriteTime = [DateTimeOffset]::new(2026, 1, 1, 0, 0, 0, [TimeSpan]::Zero)
        $destination = $entry.Open()
        $source = [System.IO.File]::OpenRead($entries[$name])
        try { $source.CopyTo($destination) }
        finally { $source.Dispose(); $destination.Dispose() }
    }
}
finally { $archive.Dispose(); $stream.Dispose() }
Write-Output "Created $outputPath ($($entries.Count) files)."

# Testing is introduced separately in lab 11, never in the starter archive.
$testingRoot = Join-Path $materialRoot 'lab11-security-testing/resources'
$testingPath = Join-Path $outputDirectory 'task-manager-testing.zip'
$testingEntries = @{
    'README.md' = Join-Path $testingRoot 'README.md'
    'compose.test.yml' = Join-Path $testingRoot 'compose.test.yml'
}
Get-ChildItem -LiteralPath (Join-Path $testingRoot 'src/test') -File -Recurse |
    ForEach-Object {
        $relative = $_.FullName.Substring($testingRoot.Length + 1).Replace('\', '/')
        $testingEntries[$relative] = $_.FullName
    }
$stream = [System.IO.File]::Open($testingPath, [System.IO.FileMode]::Create)
$archive = [System.IO.Compression.ZipArchive]::new($stream, [System.IO.Compression.ZipArchiveMode]::Create)
try {
    foreach ($name in ($testingEntries.Keys | Sort-Object)) {
        $entry = $archive.CreateEntry($name, [System.IO.Compression.CompressionLevel]::Optimal)
        $entry.LastWriteTime = [DateTimeOffset]::new(2026, 1, 1, 0, 0, 0, [TimeSpan]::Zero)
        $destination = $entry.Open()
        $source = [System.IO.File]::OpenRead($testingEntries[$name])
        try { $source.CopyTo($destination) }
        finally { $source.Dispose(); $destination.Dispose() }
    }
}
finally { $archive.Dispose(); $stream.Dispose() }
Write-Output "Created $testingPath ($($testingEntries.Count) files)."
