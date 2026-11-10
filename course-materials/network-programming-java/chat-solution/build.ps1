$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
try {
    New-Item -ItemType Directory -Path out -Force | Out-Null
    $sources = @(Get-ChildItem -LiteralPath src,test -Recurse -Filter '*.java' | ForEach-Object FullName)
    & javac --release 21 -encoding UTF-8 -d out @sources
    if ($LASTEXITCODE -ne 0) { throw 'javac failed' }
} finally {
    Pop-Location
}
