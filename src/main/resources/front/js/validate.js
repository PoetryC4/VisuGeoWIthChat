
function checkURL (rule, value, callback){
  let regURL = /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/;
  value = value.replace(/(^\s*)|(\s*$)/g, "");
  if (!regURL.test(value)) {
    callback(new Error("请输入正确的URL"))
  } else {
    callback()
  }
}
function checkIpv4 (rule, value, callback){
  let regIpv4 = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/
  value = value.replace(/(^\s*)|(\s*$)/g, "");
  if (!regIpv4.test(value)) {
    callback(new Error("请输入正确的ipv4地址"))
  } else {
    callback()
  }
}