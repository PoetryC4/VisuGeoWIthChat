
function startMonitor (params) {
  return $axios({
    url: '/monitor/open',
    method: 'get',
    params
  })
}

function closeMonitor (params) {
  return $axios({
    url: '/monitor/close',
    method: 'get',
    params
  })
}

function infoMonitor (params) {
  return $axios({
    url: '/monitor/info/ips',
    method: 'get',
    params
  })
}

function securityJudge (params) {
  return $axios({
    url: '/monitor/info/sec_judge',
    method: 'get',
    params
  })
}