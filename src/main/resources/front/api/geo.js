
function getIpData (params) {
  return $axios({
    url: '/geo/data',
    method: 'get',
    params
  })
}

