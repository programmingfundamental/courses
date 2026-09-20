param([ValidateSet('build','up','config')][string]$Action='build')
& node (Join-Path $PSScriptRoot '../../platform/tools/lab.mjs') '08' $Action
exit $LASTEXITCODE
