const firebase = require('firebase-admin');
const serviceAccount = require("./bakeryfirestore_auth.json");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://bakeryfirestore-default-rtdb.europe-west1.firebasedatabase.app"
});

const db = firebase.firestore();
const User = db.collection("items");