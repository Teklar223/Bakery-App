
//required imports
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const firebase = require('firebase-admin');
const serviceAccount = require("./bakeryfirestore_auth.json");

//firebase cradentials required for connection
firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://bakeryfirestore-default-rtdb.europe-west1.firebasedatabase.app"
});

const db = firebase.firestore();

// get all items from the firebase
app.get('/items', async (req, res) => {
    console.log("request incoming")
  const snapshot = await db.collection('items').get();
  const items = snapshot.docs.map(doc => doc.data());
  res.send(items);
});

// get all materials from the firebase
app.get('/materials', async (req, res) => {
  console.log("request incoming")
const snapshot = await db.collection('materials').get();
const materials = snapshot.docs.map(doc => doc.data());
res.send(materials);
});


//check if use is admin or not, you pass the user id in query parameter
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


app.use(bodyParser.json());

//add new item to database (as admin)
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

//add new material to database (as admin)
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