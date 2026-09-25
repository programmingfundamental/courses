# Само localhost. Точно 6 грешни опита + 1 проверка; без wordlists.
$ErrorActionPreference='Stop'
$base='http://localhost:8080'
$csrf=Invoke-RestMethod "$base/csrf" -SessionVariable session
for($i=1;$i -le 7;$i++) {
    $password=if($i -le 6){'Wrong-lab-password'}else{'Lab-alice-2026!'}
    $body=@{username='alice';password=$password;_csrf=$csrf.token}
    try {
        $r=Invoke-WebRequest "$base/login" -Method Post -Body $body -WebSession $session -UseBasicParsing
        Write-Output "Attempt $i : $($r.StatusCode)"
    } catch {
        if(-not $_.Exception.Response){throw}
        Write-Output "Attempt $i : $([int]$_.Exception.Response.StatusCode)"
    }
}
# lab04: 6 x 401, след това 204. Поправен: 7 x 401 до expiry.
