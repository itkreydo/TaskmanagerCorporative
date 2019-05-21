const express = require('express'),
http = require('http'),
https = require('https'),
app = express(),
sha256 = require('sha256'),
fs = require('fs'),
dateFormat = require('dateformat'),
crypt = require('crypto'),
mysql = require('mysql'),
server = http.createServer(app),
io = require('socket.io').listen(server);
const bodyParser = require("body-parser");
var multer  = require('multer')
var upload = multer({ dest: 'upload/'})
var maxCountFiles=10;
var connectionsCount=0;
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(bodyParser.text());


/*For https*/
var options = {
  key: fs.readFileSync('keys/server.key'),
  cert: fs.readFileSync('keys/server.crt'),
  ca: fs.readFileSync('keys/rootCA.crt'),
};
var secureServer = https.createServer(options,app);
/*end For https*/

app.get('/', (req, res) => {
res.send('Chat Server is running on port 3000')
});
app.get('/getFiles/avatar', (req, res) => {
    console.log("==getAvatar==");
    //console.log(req.query);
    var access_token = req.query.token;
    if (access_token == null){
        console.log("null token");
        res.statusCode = 404;
        res.send();
        return;
    }
    
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, auth.access_expires_in FROM auth WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;

     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     console.log("all right");
     //if token valid
        var id_userForAvatar = req.query.id_user;
    if (id_userForAvatar != null){
        console.log("avatar for u");
        id_user = id_userForAvatar;
    }
     connection.query("SELECT avatarURL FROM user WHERE id = ?",id_user, function (error, results, fields) {
    if (error) throw error;
        //console.log(results);
         //construct workers json
         var avatarURL = results[0].avatarURL;
        if (avatarURL == ""){
            res.send(200,"{status:'NO_AVATAR'}");
            return;
        }
            res.statusCode = 200;
            res.setHeader("Content-Type", "image/jpeg");
            fs.readFile(avatarURL, (err, image) => {
              res.end(image);
            });

        connection.end();
     });   

});
    

});
const urlencodedParser = bodyParser.urlencoded({extended: false});
function rawBody(req, res, next) {
    var chunks = [];

    req.on('data', function(chunk) {
        console.log(req);
        chunks.push(chunk);
    });

    req.on('end', function() {
        var buffer = Buffer.concat(chunks);

        req.bodyLength = buffer.length;
        req.rawBody = buffer;
        next();
    });

    req.on('error', function (err) {
        console.log(err);
        res.status(500);
    });
}

app.post('/upload', upload.array('files',maxCountFiles), function (req, res) {
    console.log(req);
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
  src.on('end', function() { res.send(200,{status: 'OK'}); });
  src.on('error', function(err) { res.send('error'); });
}

    } else {
        res.send(500);
    }

});

app.post('/login', (req, res) => {
console.log(req.body);

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT user.*,auth.access_token,auth.access_expires_in FROM user JOIN auth ON user.id = auth.id_user WHERE login = ?',req.body.login, function (error, results, fields) {
  if (error) throw error;

    var jsonres = {};
     if (results.length == 0){
         jsonres = {status: "NO_SUCH_USER"};
         res.send(200,JSON.stringify(jsonres));
     }else{
        var id_user=results[0]['id'];
        var fio = results[0]['surname']+' '+results[0]['name'];
        var manager=results[0]['manager'];
         var profession=results[0]['profession'];
         var avatarURL=results[0]['avatarURL'];
         if (results[0]['password'] != sha256(req.body.password)){
            jsonres = {status: "WRONG_PASSWORD"};
            res.send(200,JSON.stringify(jsonres));
            return 0;
         }
         
         var generatedToken = crypt.randomBytes(32).toString('hex');
         var expires_in_date = Math.floor(new Date() / 1000) + 60*30;
         

         
         var post  = {access_token: generatedToken,access_expires_in: expires_in_date};
         connection.query("UPDATE auth SET ? WHERE id_user=?",[post,id_user], function (error, results, fields) {if (error) throw error;});
         
         jsonres = {status: "OK", id_user:id_user, username:fio, access_token:generatedToken, expires_in: expires_in_date,manager: manager,profession: profession,avatarURL: avatarURL};
         console.log(JSON.stringify(jsonres));
         res.send(200,JSON.stringify(jsonres));
     }
     
     console.log(results);
     connection.end();
});

});

app.post('/getMyTasks', (req, res) => {
console.log("==getMyTasks==");
var access_token = req.body.access_token;
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
    connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, auth.access_expires_in FROM auth WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;
     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     var manager = results[0]['manager'];
     
     //if token valid
     connection.query('SELECT task.*,user_task.progress as user_progress FROM user_task JOIN task ON task.id = user_task.id_task WHERE user_task.id_user = ? ORDER BY date_created DESC',id_user, function (error, results, fields) {
    if (error) throw error;
        console.log(results);
         var objs = [];
         var jsonres = {};
         for (var i = 0;i < results.length; i++) {
            objs.push({id: results[i].id,title: results[i].title,description: results[i].description, date_created: results[i].date_created, date_deadline: results[i].date_deadline, status: results[i].status, user_progress:results[i].user_progress, task_progress:results[i].progress});
        }
         jsonres = {status: "OK",tasks:objs};
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
         
        connection.end();
     });

//    

});

});

app.post('/getTaskDetail', (req, res) => {
console.log("==taskDetail==");
var access_token = req.body.access_token;

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
//            connection.on('error', function(err) {
//          console.log("[mysql error]",err);
//        });
 connection.query('SELECT auth.id_user, auth.access_expires_in FROM auth WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;

     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_task = req.body.id_task;
     
     //console.log("all right");
     //if token valid
     connection.query("SELECT task.id,task.title,task.description,task.date_created,task.date_deadline,task.id_user, GROUP_CONCAT(ut.id_user) AS workersId,GROUP_CONCAT(ut.progress) AS workersProgress, GROUP_CONCAT(CONCAT(u.surname,' ',u.name)) AS fio FROM task JOIN user_task ut ON task.id=ut.id_task JOIN user u ON u.id = ut.id_user WHERE task.id=? GROUP BY task.id;",id_task, function (error, results, fields) {
    if (error) throw error;
        //console.log(results);
         //construct workers json
         var workersId = results[0].workersId.split(",");
         var workersFio = results[0].fio.split(",");
         var workersProgress = results[0].workersProgress.split(",");
         var obj = []
         for (var i=0;i<workersId.length;i++){
             obj.push({id:workersId[i],name:workersFio[i],progress:workersProgress[i]});
         }
         
         var jsonres = {};
         jsonres = {status: "OK", task:{id: results[0].id,title: results[0].title,description: results[0].description, date_created: results[0].date_created, date_deadline: results[0].date_deadline,workers: obj}};
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));

        connection.end();
     });   

});

});

app.post('/getWorkers', (req, res) => {
console.log("==getWorkers==");
//console.log(req.body);
var access_token = req.body.access_token;
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
    connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, user.manager, auth.access_expires_in FROM auth JOIN user ON user.id=auth.id_user WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;
     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     var manager = results[0]['manager'];
    if (manager !=0){
        jsonres = {status: "FORBIDDEN"};
         //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }

     //if token valid
     connection.query('SELECT user.*,COUNT(*) as num_tasks FROM user JOIN user_task ON user.id = user_task.id_user WHERE user.manager = ? GROUP BY user.id',id_user, function (error, results, fields) {
    if (error) throw error;
        //console.log(results);
        var fio;
         var objs = [];
         var jsonres = {};
         for (var i = 0;i < results.length; i++) {
            fio = results[i]['surname']+' '+results[i]['name'];
            objs.push({id: results[i].id,name: fio,profession: results[i].profession, num_tasks: results[i].num_tasks});
        }
         jsonres = {status: "OK",users:objs};
        console.log("response:");
        console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
         
        connection.end();
     });

//    

});

});

app.post('/createTask', (req, res) => {
console.log("==createTask==");
//console.log(req.body);
var access_token = req.body.access_token;
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, user.manager, auth.access_expires_in FROM auth JOIN user ON user.id=auth.id_user WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;
     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     var manager = results[0]['manager'];
    if (manager !=0){
        jsonres = {status: "FORBIDDEN"};
         //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var title = req.body.title;
     var description = req.body.description;
     var date_deadline = req.body.date_deadline;
     var date_created = dateFormat(new Date(),"yyyy-mm-dd");
     var workers = JSON.parse(req.body.workers);
     //if token valid
    var post  = {title: title, description: description,date_deadline: date_deadline,date_created:date_created,id_user: id_user};
     
    connection.query("INSERT INTO task SET ?",post, function (error, results, fields) {
    if (error) throw error;
    var insertedId = results.insertId;
    var post_array = [];
        post_array.push([id_user,insertedId]);
    for (var i=0;i<workers.length;i++){
        post_array.push([workers[i]["id"],insertedId]);
    }
    console.log(post_array);
        connection.query("INSERT INTO user_task(id_user,id_task) VALUES ?",[post_array], function (error, results, fields) {
            if (error) throw error;


            jsonres = {status: "OK"};
            res.send(200,JSON.stringify(jsonres));
            connection.end();
        });

    });

//    

});

});

app.post('/checkTask', (req, res) => {
console.log("==checkTask==");
//console.log(req.body);
var access_token = req.body.access_token;
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, user.manager, auth.access_expires_in FROM auth JOIN user ON user.id=auth.id_user WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;
     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     var manager = results[0]['manager'];
    var id_task = req.body.id_task;
     var progress = req.body.progress;
     var post  = {progress: progress};
     
    if (manager == 0){
        connection.query("UPDATE user_task SET ? WHERE id_task =?",[post,id_task], function (error, results, fields) {
    if (error) throw error;
        jsonres = {status: "OK"};
        //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        connection.end();
    });
        return;
     }

     
     
    
     
    connection.query("UPDATE user_task SET ? WHERE id_user=? and id_task =?",[post,id_user,id_task], function (error, results, fields) {
    if (error) throw error;
        jsonres = {status: "OK"};
        //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        connection.end();
    });

//    

});

});

app.post('/editTask', (req, res) => {
console.log("==editTask==");
console.log(req.body);
var access_token = req.body.access_token;
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, user.manager, auth.access_expires_in FROM auth JOIN user ON user.id=auth.id_user WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;
     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     var manager = results[0]['manager'];
    if (manager !=0){
        jsonres = {status: "FORBIDDEN"};
         //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_task = req.body.id_task;
     var title = req.body.title;
     var description = req.body.description;
     var date_deadline = req.body.date_deadline;
//     var date_created = dateFormat(new Date(),"yyyy-mm-dd");
    var workers = JSON.parse(req.body.workers);
     //if token valid
    var post  = {title: title, description: description,date_deadline: date_deadline,id_user: id_user};
     
    connection.query("UPDATE task SET ? WHERE id = ?",[post,id_task], function (error, results, fields) {
    if (error) throw error;
        var workersArrayNew = []; 
        var workersArrayDel = []; 
        for (var i=0;i<workers.length;i++){
            if (workers[i]["type"]=="new")
                workersArrayNew.push([workers[i]["id"],id_task]);
            if (workers[i]["type"]=="del")
                workersArrayDel.push(workers[i]["id"]);
        }

            if (workersArrayDel.length !=0)
                connection.query("DELETE FROM user_task WHERE id_user IN (?) and id_task = ?",[workersArrayDel,id_task], function (error, results, fields) {
                        if (error) throw error;
                });
            if (workersArrayNew.length !=0)
                connection.query("INSERT INTO user_task(id_user,id_task) VALUES ?",[workersArrayNew], function (error, results, fields) {
                    if (error) throw error;
                });

            jsonres = {status: "OK"};
            res.send(200,JSON.stringify(jsonres));
            connection.end();
    });

//    

});

});

app.post('/deleteTask', (req, res) => {
console.log("==deleteTask==");
//console.log(req.body);
var access_token = req.body.access_token;
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
connection.query("SET SESSION wait_timeout = 604800");
 connection.query('SELECT auth.id_user, user.manager, auth.access_expires_in FROM auth JOIN user ON user.id=auth.id_user WHERE access_token = ?',access_token, function (error, results, fields) {
  if (error) throw error;
     var jsonres = {};
     if (results.length==0){
         jsonres = {status: "UNKNOWN_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
         res.send(200,JSON.stringify(jsonres));
         return;
     }
     var access_expire_in = results[0]['access_expires_in'];
     var dateNow = new Date()/1000;
     if (results[0]['access_expires_in'] < dateNow){
        jsonres = {status: "OLD_TOKEN"};
          //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
     var id_user = results[0]['id_user'];
     var manager = results[0]['manager'];
    if (manager !=0){
        jsonres = {status: "FORBIDDEN"};
         //console.log("response:");
        //console.log(JSON.stringify(jsonres));
        res.send(200,JSON.stringify(jsonres));
        return;
     }
    //if token valid and it is manager    
     var id_task = req.body.id_task;

     
    connection.query("DELETE FROM task WHERE id = ? ",id_task, function (error, results, fields) {
    if (error) throw error;

            jsonres = {status: "OK"};
            res.send(200,JSON.stringify(jsonres));
            connection.end();


    });

//    

});

});

server.listen(3000,()=>{
console.log('Node app is running on port 3000')
});

secureServer.listen(443,()=>{
console.log('Node app is running on port 443')
});




















// middleware
io.use((socket, next) => {
    let access_token = socket.handshake.query.token;
    
    var connection = mysql.createConnection({
      host     : 'localhost',
      user     : 'root',
      password : '',
      database : 'taskmanager'
    }); 
    connection.connect(function(err) {
      if (err) {
        console.error('error connecting: ' + err.stack);
        return;
      }
      console.log('connected as id ' + connection.threadId);
    });
    connection.query("SET SESSION wait_timeout = 604800");
     connection.query('SELECT auth.id_user, user.manager, auth.access_expires_in FROM auth JOIN user ON user.id=auth.id_user WHERE access_token = ?',access_token, function (error, results, fields) {
      if (error) throw error;
         connection.end();
         var jsonres = {};
         if (results.length==0){
             jsonres = {status: "UNKNOWN_TOKEN"};
              console.log("response:");
            //console.log(JSON.stringify(jsonres));
//             res.send(200,JSON.stringify(jsonres));
//             return;
               return next(new Error('authentication error'));
         }
         var access_expire_in = results[0]['access_expires_in'];
         var dateNow = new Date()/1000;
         if (results[0]['access_expires_in'] < dateNow){
            jsonres = {status: "OLD_TOKEN"};
              console.log("response:");
            //console.log(JSON.stringify(jsonres));
//            res.send(200,JSON.stringify(jsonres));
//            return;
            return next(new Error('authentication error'));
         }
         console.log("oke");
         return next(); 
     });


});

/*WEB SSOCKET SERVER*/
io.on('connection', (socket) => {
let access_token = socket.handshake.query.token;
connectionsCount++;
console.log('user connected '+access_token);

socket.on('join', function(userNickname,id_task) {
    socket.join(id_task);
console.log(userNickname +" : has joined the chat in  task "+id_task);
socket.broadcast.to(id_task).emit('userjoinedthechat',userNickname +" : has   joined the chat ");

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'taskmanager'
}); 
connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
});
 connection.query('SELECT task_chat.*,user.name,user.surname FROM task_chat JOIN user ON user.id = task_chat.id_sender WHERE task_chat.id_task=? ORDER BY task_chat.date ',id_task, function (error, results, fields) {
  if (error) throw error;
    
     
    var objs = [];
     var jsonres = {};
    for (var i = 0;i < results.length; i++) {
        objs.push({id_user: results[i].id_sender, username: results[i].surname +" "+ results[i].name,message: results[i].text,date: results[i].date});
    }
     jsonres = {messages:objs};
     console.log(JSON.stringify(jsonres));
    socket.emit("updateDialog",JSON.stringify(jsonres));
     
  // connected!
});
connection.end();

    })
    
    socket.on('messagedetection', (messageJson,id_task) => {   
        var message = JSON.parse(messageJson);
        console.log(message);
           //log the message in console 
           console.log(message.user.nickname+" : " +message.message.text);        
          //add to database
        var connection = mysql.createConnection({
      host     : 'localhost',
      user     : 'root',
      password : '',
      database : 'taskmanager'
    }); 
    connection.connect(function(err) {
      if (err) {
        console.error('error connecting: ' + err.stack);
        return;
      }
      console.log('connected as id ' + connection.threadId);
    });
    var post  = {id_task:id_task,id_sender:message.user.id,text: message.message.text};
     connection.query("INSERT INTO task_chat SET ?",post, function (error, results, fields) {
      if (error) throw error;

      // connected!
    });
    connection.end();


    // send the message to all users including the sender  using io.emit         
    socket.broadcast.to(id_task).emit('message', JSON.stringify(message) )  

    })
    
    socket.on('disconnect', function() {
         connectionsCount--;
        console.log(' has left ')
        socket.broadcast.emit( "userdisconnect" ,' user has left')
    })

})



//app.post('/crypto', (req, res) => {
//console.log("== crypto ==");
//    console.log(req.body);
//    decodeBody(req.body);
//jsonres = {status: "OKK"};
//console.log("response:");
//console.log(JSON.stringify(jsonres));
//res.send(200,JSON.stringify(jsonres));
//});
//
//
//function checkToken(access_token){
//var connection = mysql.createConnection({
//  host     : 'localhost',
//  user     : 'root',
//  password : '',
//  database : 'taskmanager'
//}); 
//connection.connect(function(err) {
//  if (err) {
//    console.error('error connecting: ' + err.stack);
//    return;
//  }
//  console.log('connected as id ' + connection.threadId);
//});
//    
// connection.query('SELECT auth.id_user, auth.access_expires_in FROM auth WHERE access_token = ?',access_token, function (error, results, fields) {
//  if (error) throw error;
//     var jsonres = {};
//     if (results.length==0){
//         jsonres = {status: "UNKNOWN_TOKEN"};
//         return jsonres;
//     }
//     var access_expire_in = results[0]['access_expires_in'];
//     var dateNow = new Date()/1000;
//     if (results[0]['access_expires_in'] < dateNow){
//        jsonres = {status: "OLD_TOKEN"};
//        return jsonres;
//     }
//     return {status: "OK"};
// });
//connection.end();
//};
//app.get('/testy', function(req, res){
//  res.send('<form method="post" action="/upload" enctype="multipart/form-data">'
//           + '<p>Image: <input type="file" name="test" /></p>'
//           + '<input type="text" name="testSTRING" value="hello OOOOOO" />'
//           + '<input type="text" name="testSTRING2" value="hello kk" />'
//           + '<p><input type="submit" value="Upload" /></p>'
//           + '</form>');
//});
//function rawBody(req, res, next) {
//    var chunks = [];
//
//    req.on('data', function(chunk) {
//        chunks.push(chunk);
//
//    });
//
//    req.on('end', function() {
//        var buffer = Buffer.concat(chunks);
//
//        req.bodyLength = buffer.length;
//        req.rawBody = buffer;
//        console.log("in Raw"+buffer);
//        //next();
//    });
//
//    req.on('error', function (err) {
//        console.log(err);
//        res.status(500);
//    });
//}