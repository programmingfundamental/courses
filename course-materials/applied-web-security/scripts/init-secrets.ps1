$ErrorActionPreference = 'Stop'
$courseRoot = Split-Path -Parent $PSScriptRoot
$secretDir = Join-Path $courseRoot '.secrets'
New-Item -ItemType Directory -Force -Path $secretDir | Out-Null
$keyPath = Join-Path $secretDir 'field-key.txt'
if (-not (Test-Path -LiteralPath $keyPath)) {
    $bytes = New-Object byte[] 32
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($bytes)
    $rng.Dispose()
    [IO.File]::WriteAllText($keyPath, [Convert]::ToBase64String($bytes))
}
Write-Output 'Лабораторният ключ е готов; стойността не се показва.'
