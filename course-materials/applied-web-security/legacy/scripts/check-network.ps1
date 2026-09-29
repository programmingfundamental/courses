# Приложен configuration invariant; не стартира и не променя containers.
$ErrorActionPreference='Stop'
Push-Location (Split-Path -Parent $PSScriptRoot)
try {
    $raw=& docker compose config --format json
    if($LASTEXITCODE -ne 0){throw 'Compose config failed'}
    $config=$raw | ConvertFrom-Json
    if($config.services.app.ports -or $config.services.db.ports){throw 'DB/app must not publish host ports'}
    if(-not $config.networks.backend.internal){throw 'Backend must be internal'}
    foreach($port in $config.services.nginx.ports){if($port.host_ip -ne '127.0.0.1'){throw 'Only loopback port publishing allowed'}}
    if(@($config.services.nginx.ports).Count -ne 2){throw 'Unexpected proxy port count'}
    Write-Output 'PASS: DB/app private, backend internal, two loopback proxy ports.'
} finally {Pop-Location}
