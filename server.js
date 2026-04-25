const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();


app.use(cors());
app.use(express.json());


const authRoutes = require('./routes/authRoutes');


app.use('/api/auth', authRoutes); // This mounts your auth routes!


app.get('/', (req, res) => {
    res.send('AbilityBridge API is running...');
});


const PORT = process.env.PORT || 3000;
// Adding '0.0.0.0' FORCES the server to accept connections from other devices on the Wi-Fi
app.listen(PORT, '0.0.0.0', () => {
    console.log(` Server running on port ${PORT} (Accepting outside connections)`);
});
