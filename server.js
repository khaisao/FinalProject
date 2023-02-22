const express = require("express");
const http = require("http");
const socket = require("socket.io");
const QRCode = require("qrcode");

const app = express();
const port = process.env.PORT || 3000;
const server = app.listen(port)

const io = socket(server);
app.use(express.static(__dirname));


// app.get("/", (req, res) => {
//     res.sendFile(__dirname + "/index.html");
// });

io.on("connection", (socket) => {
    const ipAddress = socket.handshake.address;
    const url = `ws://${ipAddress}:${port}`;
    console.log("new connection" + socket.id)
    socket.on("image", () => {
        console.log('Nhan du lieu')
        // console.log(data.image); // in ra chuỗi base64 của ảnh được gửi từ client
    });
    QRCode.toDataURL(url, (err, src) => {
        if (err) return console.error(err);
        socket.emit("qr code", src);
    });
});


