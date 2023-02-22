const socket = io();

const qr = new QRCode(document.getElementById("qrcode"), {
    width: 256,
    height: 256,
});

socket.on("connect", () => {
    const ipAddress = window.location.hostname;
    const port = window.location.port;
    const url = `ws://${ipAddress}:${port}`;
    qr.makeCode(url);
});


socket.on("connect", () => {
    const ipAddress = window.location.hostname;
    const port = window.location.port;
    const url = `ws://${ipAddress}:${port}`;
    const ws = new WebSocket(url);

    ws.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log(data);
    };

    socket.on("message", (data) => {
        ws.send(JSON.stringify(data));
    });
});