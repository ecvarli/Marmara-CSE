const express = require('express');
const router = express.Router();
const fs = require('fs');
const path = require('path');

const usersDataPath = path.join(__dirname, '../data/users.json');
let users = JSON.parse(fs.readFileSync(usersDataPath, 'utf-8'));

// Middleware to find user by username (assuming user is logged in and username is known)
function findUser(req, res, next) {
  const username = req.session.username || req.query.username || req.body.username;
  const user = users.find(u => u.username === username);
  if (!user) {
    return res.status(404).json({ message: 'User not found' });
  }
  req.user = user;
  next();
}

router.post('/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find(u => u.username === username && u.password === password);
  if (!user) {
    return res.status(401).json({ message: 'Invalid username or password' });
  }
  req.session.username = username;
  res.json({ message: 'Login successful', user });
});

router.post('/signup', (req, res) => {
  const { username, password } = req.body;
  const userExists = users.some(u => u.username === username);
  if (userExists) {
    return res.status(409).json({ message: 'Username already exists' });
  }
  const newUser = { username, password, age: null, weight: null, height: null, bmi: null };
  users.push(newUser);
  fs.writeFileSync(usersDataPath, JSON.stringify(users, null, 2));
  res.json({ message: 'Signup successful', user: newUser });
});

router.get('/profile', findUser, (req, res) => {
  res.json(req.user);
});

router.post('/profile', findUser, (req, res) => {
  const { age, weight, height } = req.body;
  req.user.age = age;
  req.user.weight = weight;
  req.user.height = height;

  // Calculate BMI
  if (weight && height) {
    req.user.bmi = (weight / ((height / 100) ** 2)).toFixed(2); // Correctly calculate BMI using height in cm
  }

  fs.writeFileSync(usersDataPath, JSON.stringify(users, null, 2));
  res.json({ message: 'Profile updated successfully', user: req.user });
});

router.post('/profile/bmi', findUser, (req, res) => {
  const { bmi } = req.body;
  req.user.bmi = bmi;

  fs.writeFileSync(usersDataPath, JSON.stringify(users, null, 2));
  res.json({ message: 'BMI updated successfully', user: req.user });
});

module.exports = router;
