const net = require('net');
const sharp = require('sharp');
const fs = require('fs');

let data = Buffer.alloc(0);


const server = net.createServer((socket) => {
    console.log('Client connected');

    socket.on('data', function(chunk) {
        // Append the new chunk to the existing data
        data = Buffer.concat([data, chunk]);

        // Check if the null terminator is present in the new chunk
        const nullTerminatorIndex = data.indexOf(0x00);
        if (nullTerminatorIndex >= 0) {
            // Extract the message from the buffer and remove the null terminator
            const message = data.toString('utf8', 0, nullTerminatorIndex);

            console.log(`Received message: ${message}`);

            // Extract the base64-encoded image data from the message
            const imageData = message.substring(message.indexOf(',') + 1);

            // Decode the base64-encoded data
            const decodedData = Buffer.from(imageData, 'base64');

            // Write the decoded data to a file
            fs.writeFile('image.jpg', decodedData, function(err) {
                if (err) throw err;
                console.log('Image saved successfully');
            });

            // Remove the processed message from the data buffer
            data = data.slice(nullTerminatorIndex + 1);
        }
    });

    socket.on('end', () => {
        // console.log(`Received data: ${buffer}`);
        console.log('Client disconnected');
    });
});

server.on('error', (err) => {
    throw err;
});

server.listen(8080, () => {
    console.log('Server listening on port 8080');
});
