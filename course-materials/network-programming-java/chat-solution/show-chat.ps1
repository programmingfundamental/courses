param(
    [ValidateRange(1024, 65535)][int]$Port = 9000,
    [ValidateRange(1024, 65535)][int]$UdpPort = 9001
)
$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
$launched = [System.Collections.Generic.List[System.Diagnostics.Process]]::new()
try {
    & ./build.ps1
    $portCheck = [Net.Sockets.TcpListener]::new([Net.IPAddress]::Loopback, $Port)
    try { $portCheck.Start() } finally { $portCheck.Stop() }
    $javaExe = (Get-Command java -CommandType Application | Select-Object -First 1).Source
    function Start-ChatProcess([string[]]$Arguments) {
        # Each process owns an interactive terminal for the live demonstration.
        $process = Start-Process -FilePath $javaExe -ArgumentList $Arguments -WorkingDirectory $PSScriptRoot -PassThru
        $launched.Add($process)
        return $process
    }

    $serverArgs = @('-cp', 'out', 'labnet.ChatServer', '--port', "$Port", '--udp-port', "$UdpPort")
    $server = Start-ChatProcess $serverArgs

    $until = [DateTime]::UtcNow.AddSeconds(30)
    do {
        if ($server.HasExited) { throw "Server exited; check TCP port $Port" }
        $probe = [System.Net.Sockets.TcpClient]::new()
        try { $probe.Connect('localhost', $Port); $ready = $true }
        catch { $ready = $false; Start-Sleep -Milliseconds 100 }
        finally { $probe.Dispose() }
    } until ($ready -or [DateTime]::UtcNow -ge $until)
    if (!$ready -or $server.HasExited) { throw "Server did not start on port $Port" }

    foreach ($name in 'ana', 'boris', 'iva') {
        $clientArgs = @('-cp', 'out', 'labnet.ChatClient', '--port', "$Port", '--udp-port', "$UdpPort", '--name', $name)
        $null = Start-ChatProcess $clientArgs
    }
    Write-Host "One server on localhost:$Port (UDP $UdpPort). Wait for 'Connected' in all three clients."
    Write-Host 'Use /quit in each console to finish.'
} catch {
    foreach ($process in $launched) {
        if (!$process.HasExited) { $process.Kill() }
    }
    throw
} finally {
    Pop-Location
}
