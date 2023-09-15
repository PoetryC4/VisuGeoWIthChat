sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay))

async function downloadFile(fileUrl,fileName) {
    let blob = await getBlob(fileUrl);
    saveFile(blob, fileName);
}
function getBlob(fileUrl) {
    return new Promise(resolve => {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', fileUrl, true);
        //监听进度事件
        xhr.addEventListener(
            'progress',
            function (evt) {
                if (evt.lengthComputable) {
                    let percentComplete = evt.loaded / evt.total;
                    // percentage是当前下载进度，可根据自己的需求自行处理
                    let percentage = percentComplete * 100;
                }
            },
            false
        );
        xhr.responseType = 'blob';
        xhr.onload = () => {
            if (xhr.status === 200) {
                resolve(xhr.response);
            }
        };
        xhr.send();
    });
}
function saveFile(blob, fileName) {
    // ie的下载
    if (window.navigator.msSaveOrOpenBlob) {
        navigator.msSaveBlob(blob, filename);
    } else {
        // 非ie的下载
        const link = document.createElement('a');
        const body = document.querySelector('body');

        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;

        // fix Firefox
        link.style.display = 'none';
        body.appendChild(link);

        link.click();
        body.removeChild(link);

        window.URL.revokeObjectURL(link.href);
    }
}
