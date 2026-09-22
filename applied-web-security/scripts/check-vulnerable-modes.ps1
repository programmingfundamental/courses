# Преподавателска проверка: дефектите трябва да причинят assertion failures.
# След като студентът ги поправи, този diagnostic runner вече не е приложим.
$ErrorActionPreference='Stop'
$app=Join-Path (Split-Path -Parent $PSScriptRoot) 'vulnerable-app'
$cases=@{
    lab02='WebSecurityTest#lab02_passwordStorage'
    lab03='WebSecurityTest#lab03_objectAndRoleMatrix'
    lab04='WebSecurityTest#lab04_limitIsWiredIntoLogin'
    lab05='WebSecurityTest#lab05_sqlInjectionCannotCrossOwnerBoundary'
    lab06='WebSecurityTest#lab06_storedAndReflectedEncoding'
    lab07='WebSecurityTest#lab07_csrfMustProtectState'
    lab08='WebSecurityTest#lab08_sensitiveFieldNotPlaintext'
    lab09='JwtSecurityTest#modifiedPayloadAndForeignSignature'
    lab10='WebSecurityTest#lab03_objectAndRoleMatrix'
}
Push-Location $app
try {
    foreach($mode in ($cases.Keys | Sort-Object)) {
        $log=Join-Path 'target' "$mode-red.log"
        New-Item -ItemType Directory -Force target | Out-Null
        & mvn -B test "-Dlab.mode=$mode" "-Dtest=$($cases[$mode])" *> $log
        $exit=$LASTEXITCODE
        $output=Get-Content $log -Raw
        if($exit -eq 0 -or $output -notmatch 'Failures: [1-9]\d*, Errors: 0'){throw "$mode did not produce the expected assertion failure; inspect $log"}
        Write-Output "PASS: $mode detected by assertion failure"
    }
} finally { Pop-Location }
