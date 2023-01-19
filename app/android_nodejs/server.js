
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const firebase = require('firebase-admin');
const serviceAccount = require("./bakeryfirestore_auth.json");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://bakeryfirestore-default-rtdb.europe-west1.firebasedatabase.app"
});

const db = firebase.firestore();

app.get('/items', async (req, res) => {
    console.log("request incoming")
  const snapshot = await db.collection('items').get();
  const items = snapshot.docs.map(doc => doc.data());
  res.send(items);
});

app.get('/materials', async (req, res) => {
  console.log("request incoming")
const snapshot = await db.collection('materials').get();
const materials = snapshot.docs.map(doc => doc.data());
res.send(materials);
});

app.get('/orders', async (req, res) => {
  console.log("request incoming")
const snapshot = await db.collection('orders').get();
const orders = snapshot.docs.map(doc => doc.data());
res.send(orders);
});

app.get('/adminCheck', async (req, res) => {
  console.log("request incoming")
  let id = undefined;
  if(req?.query){
    id = req.query.userID
  }
  else{
    res.send(false)
  }
  const snapshot = await db.collection('admins').get();
  const admins = snapshot.docs.map(doc => doc.id);
  let result = false;
  for(let item of admins){
    if(item == id){
      result = true
      break;
    }
  }
  res.send(result);
});

app.get('/login', async (req, res) => {
  console.log("request incoming")
  let username = undefined;
  let password = undefined;
  if(req?.query){
    username = req.query.username
    password = req.query.password
  }
  else{
    res.send(false)
  }
  const snapshot = await db.collection('admins').get();
  const admins = snapshot.docs.map(doc => doc.id);
  let result = false;
  for(let item of admins){
    if(item == id){
      result = true
      break;
    }
  }
  res.send(result);
});


app.use(bodyParser.json());

app.post('/addItem', (req, res) => {
  console.log("request incoming")
  const itemData = req.body;
  db.collection('items').add(itemData)
    .then(() => {
      res.send({ success: true, itemId: ref.id });
    })
    .catch(err => {
      res.status(500).send({ success: false, error: err });
    });
});

app.post('/addMaterial', (req, res) => {
  console.log("request incoming")
  const materialData = req.body;
  db.collection('materials').add(materialData)
    .then(() => {
      res.send({ success: true, materialId: ref.id });
    })
    .catch(err => {
      res.status(500).send({ success: false, error: err });
    });
});

app.listen(3000, () => {
  console.log('Server is running on port 3000');
});