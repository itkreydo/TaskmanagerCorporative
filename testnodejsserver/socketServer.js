io.on('connection', (socket) => {
connectionsCount++;
console.log('user connected')

socket.on('join', function(userNickname) {
console.log(userNickname +" : has joined the chat "  );
socket.broadcast.emit('userjoinedthechat',userNickname +" : has   joined the chat ");

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'dps'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
 connection.query('SELECT * FROM message', function (error, results, fields) {
  if (error) throw error;
    
     
    var objs = [];
     var jsonres = {};
    for (var i = 0;i < results.length; i++) {
        if (results[i].message!="")
            objs.push({username: results[i].nickname,message: results[i].message, date: results[i].date});
        else
            objs.push({username: results[i].nickname,file: results[i].file, date: results[i].date});
    }
     jsonres = {messages:objs};
     //console.log(JSON.stringify(jsonres));
    socket.emit("updateDialog",JSON.stringify(jsonres));
     
  // connected!
});
connection.end();

    })
    
socket.on('messagedetection', (senderNickname,messageContent,messageTime) => {       
       //log the message in console 
       console.log(senderNickname+" : " +messageContent)       
      //create a message object       
      let  message = {"message":messageContent, 
                      "senderNickname":senderNickname,
                      "messageTime":messageTime} 
      //add to database
      var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'dps'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
var post  = {message: messageContent, nickname: senderNickname, date: messageTime};
 connection.query("INSERT INTO message SET ?",post, function (error, results, fields) {
  if (error) throw error;
     
  // connected!
});
connection.end();
      
      
// send the message to all users including the sender  using io.emit         
socket.broadcast.emit('message', message)   
})
     socket.on('disconnect', function() {
         connectionsCount--;
        //console.log(userNickname +' has left ')
        socket.broadcast.emit( "userdisconnect" ,' user has left')
    })
    socket.on('image', function() {
         app.post('/upload', upload.array('files',maxCountFiles), function (req, res) {
            if (req.body) { 
                // TODO save image (req.rawBody) somewhere 
                console.log("Files: "+req.files.length); 
                if (req.files.length != 0) { 
                    var tmp_path = req.files[0].path; 

                    /** The original name of the uploaded file 
                    stored in the variable "originalname". **/ 
                    var target_path = 'uploads/' + req.files[0].originalname; 

                    /** A better way to copy the uploaded file. **/ 
                    var src = fs.createReadStream(tmp_path); 
                    var dest = fs.createWriteStream(target_path); 
                    src.pipe(dest);

                    var connection = mysql.createConnection({
                      host     : 'localhost',
                      user     : 'root',
                      password : '',
                      database : 'dps'
                    }); 
                    connection.connect();

                    var post  = {nickname: req.body.username, file: target_path, date: req.body.date};
                    let  message = {"file":req.files[0].originalname, 
                              "senderNickname":req.body.username,
                              "messageTime":req.body.date}
                    connection.query("INSERT INTO message SET ?",post, function (error, results, fields) {
                      if (error) throw error;
                    });
                    connection.end();
                    socket.broadcast.emit('message', message)
                    src.on('end', function() { res.send(200,{status: 'OK'}); }); 
                    src.on('error', function(err) { res.send('error'); }); 
                } 

            } else { 
                res.send(500); 
            } 
         });
    })
})