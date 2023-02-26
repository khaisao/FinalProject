const net = require('net');
const sharp = require('sharp');
const fs = require('fs');
const path = require("path");

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

            const fileData = message.substring(message.indexOf(',') + 1);
            // Extract the file name from the message
            const fileName = message.substring(0, message.indexOf(','));

            // Decode the base64-encoded data
            const decodedData = Buffer.from(fileData, 'base64');

            // Write the decoded data to a file
            const filePath = path.join(__dirname, 'uploads', fileName);
            const dirPath = path.dirname(filePath);
            fs.mkdir(dirPath, { recursive: true }, function(err) {
                if (err) {
                    console.error(`Error creating directory ${dirPath}: ${err}`);
                } else {
                    fs.writeFile(filePath, decodedData, function(err) {
                        if (err) {
                            console.error(`Error saving file ${fileName}: ${err}`);
                        } else {
                            console.log(`File ${fileName} saved successfully`);
                        }
                    });
                }
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
