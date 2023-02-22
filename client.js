const socket = io();
socket.on("connect", () => {
    console.log("Connected to server");
});

socket.on("qr code", (src) => {
    const qrcode = document.getElementById("qrcode");
    qrcode.innerHTML = `<img src="${src}" alt="QR Code" />`;
});