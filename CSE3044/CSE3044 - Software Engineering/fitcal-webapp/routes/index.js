const express = require('express');
const router = express.Router();

router.use('/auth', require('./auth'));
router.use('/foods', require('./foods'));

module.exports = router;
