const express = require('express');
const router = express.Router();
const fs = require('fs');
const path = require('path');

const foodsDataPath = path.join(__dirname, '../data/food_calories.json');
let foods = JSON.parse(fs.readFileSync(foodsDataPath, 'utf-8'));

router.get('/foods', (req, res) => {
  res.json(foods);
});

module.exports = router;
