
///////////////////////
function loadInterface(savedStuff){ 
    if(savedStuff != null){
        gui = new dat.GUI({load: savedStuff, preset: 'default'})
    }
    else{
        gui = new dat.GUI();
    }
    gui.width = 300;
    gui.remember(config);
    gui.remember(sageRequest);
    gui.remember(rotationMapInput);
    gui.remember(coord);
    gui.remember(seq);
    gui.remember(builtInSeq);

    const canvasControllerGUI = gui.addFolder('controller');
    canvasControllerGUI.add(builtInSeq, 'apply').name("Load built-in");
    canvasControllerGUI.add(sageRequest, 'sendRequest').name('Load /w OEIS');
    canvasControllerGUI.add(canvasController, 'startDraw').name('Start/Resume drawing');
    canvasControllerGUI.add(canvasController, 'pauseDraw').name('Pause drawing');
    canvasControllerGUI.add(canvasController, 'resetDraw').name('Reset canvas');
    canvasControllerGUI.add(canvasController, 'applyAll').name('Apply All');

    const seqFolderGUI = gui.addFolder('Sequence');
    seqFolderGUI.add(seq, 'count').name('No of Elements');
    seqFolderGUI.add(seq, 'mod');
    seqFolderGUI.add(builtInSeq, 'chosenSeq', Object.keys(builtInSeq.availableSeqs)).name("Built In Seq");
    seqFolderGUI.add(sageRequest, 'sloaneID').name('OEIS ID');

    const rotationMapGUI = gui.addFolder('Rotation Map');
    rotationMapGUI.add(rotationMapInput, 'keys').name('Element Input');
    rotationMapGUI.add(rotationMapInput, 'vals').name('Angle Output');
    rotationMapGUI.add(rotationMapInput, 'apply').name('Apply Map');

    const coordGUI = gui.addFolder('Coordinates');
    coordGUI.add(coord, 'initX').name('Initial X');
    coordGUI.add(coord, 'initY').name('Initial Y');
    coordGUI.add(coord, 'initOrientation').name('Initial Angle');
    coordGUI.add(coord, 'reset').name('Apply');

    const configGUI = gui.addFolder('Settings');
    configGUI.add(config,'canvasHeight').name('Canvas Height');
    configGUI.add(config,'canvasWidth').name('Canvas Width');
    configGUI.add(config,'transparentBg').name('Transparent background');
    configGUI.addColor(config,'bgColor').name('Bg Color');
    configGUI.addColor(config,'lineColor').name('Line color');
    configGUI.add(config,'lineWeight').name('Line thickness');
    configGUI.add(config,'unitStep').name('Size of step');
    configGUI.add(config,'strokeCap', stroketypes).name('Stroke modes');
    configGUI.add(config,'blendMode', blendmodes).name('Blend Types');
    configGUI.add(config,'reset').name('Apply');

    const persistGUI = gui.addFolder('JSON');
    persistGUI.add(persistData, "jsonLoad").name("Load JSON");
    persistGUI.add(persistData, "jsonGenerate").name("Generate JSON");
}
function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}
//Classes and global objects
class SEQ {
    constructor(){
        this.list = [];
        this.mod = 5;
        this.count = 10000;
    }
}
//To add a generator for a sequence
//1. Define the generator inside BUILTINSEQ, it should the following signature: f(n, m){ return Array}, 
//n is how many elements to return and m is a mod, you can ignore m. 
//2. Once you've defined the function add it to this.availableSeqs as key/value pair where the value is the function reference.
class BUILTINSEQ {
    constructor(){
        this.chosenSeq = this.fibonacci;
        this.availableSeqs = {'fibonacci': this.fibonacci, 'lucas': this.lucas};
    }
    apply(){
        if(this.chosenSeq){
            seq.list = this.availableSeqs[this.chosenSeq](seq.count, seq.mod);
        }
    }
    //Define sequences here to test out, and then add them to availableSeqs so you can select them.
    fibonacci(n,m){
        var fib = [0,1];
        while(n > 0){
            fib.push((fib[fib.length - 1] + fib[fib.length - 2]) % m);
            n--;
        }
        return fib;    
    }
    lucas(n,m){
        var luc = [2,1];
        while(n > 0){
            luc.push((luc[luc.length - 1] + luc[luc.length - 2]) % m);
            n--;
        }
        return luc;
    }
    ///////////////////////////
}
class SAGEREQUEST {
    constructor(){
        this.sloaneID = "A010060";
    }
    sendRequest(){
        xhr.open( 'POST', 'https://sagecell.sagemath.org/service', true );
        xhr.setRequestHeader( 'content-type', 'application/x-www-form-urlencoded' );
        xhr.send( "code=" + encodeURIComponent( "print(str(sloane." + this.sloaneID + ".list(" + seq.count.toString() + ")))" ) );
    }
}
class ROTATIONMAPINPUT {
    constructor(){
        this.keys = "0,1,2,3,4";
        this.vals = "10,20,50,-71,60";
    }
    apply(){
        var keys = JSON.parse("[" + this.keys + "]");
        var vals = JSON.parse("[" + this.vals + "]");
        if( keys.length != vals.length){
            alert("Unequal number of keys and values");
        }
        rotationMap = {};
        keys.forEach( (key, i) => rotationMap[key] = parseInt(vals[i])*(Math.PI /180));
    }
}
class COORD {
    constructor(){
        this.initX = 300;
        this.initY = 300;
        this.initOrientation = 0;
        this.X = 0;
        this.Y = 0;
        this.orientation = 0;
        this.i = 0;
    }
    reset(){
        this.X = this.initX;
        this.Y = this.initY;
        this.orientation = this.initOrientation*(Math.PI/180);
        this.i = 0;
    }
}
class CONFIG {
    constructor() {
        this.canvasHeight = 800;
        this.canvasWidth = 800;
        this.transparentBg = false;
        this.bgColor = "#000064";
        this.lineColor = "#b8b8b8";
        this.lineWeight = 2;
        this.lineType
        this.initX = 300;
        this.initY = 300;
        this.initOrientation = 0;
        this.unitStep = 20;
        this.blendMode = null;
        this.strokeCap = null;
        this.renderer = null;
    }
    reset(){
            createCanvas(this.canvasWidth, this.canvasHeight, this.renderer);
            if(this.transparentBg){
                clear();
            }
            else{
                background(this.bgColor);
            }
            stroke(this.lineColor);
            strokeWeight(this.lineWeight);
            blendMode(this.blendMode);
            strokeCap(this.strokeCap);
    }
};
const persistData = {
    jsonLoad: function(){
        jsonToLoad = document.getElementById("loadJson").value;
        if(IsJsonString(jsonToLoad)){ 
            gui.destroy();
            loadInterface( JSON.parse(jsonToLoad));
        }
        else{
            alert("Invalid JSON!");
        }
    },
    jsonGenerate: function(){ 
        document.getElementById("GeneratedJson").value = JSON.stringify(gui.getSaveObject());
    }
};
const canvasController = {
    applyAll: function applyAll(){
        coord.reset();
        config.reset();
        rotationMapInput.apply();
    },
    startDraw: function startDraw(){
        loop();
    },
    pauseDraw: function pauseDraw(){
        noLoop();
    },
    resetDraw: function resetDraw(){
        noLoop();
        coord.reset();
        config.reset();
    }
};
//

//Global Variables
const xhr = new XMLHttpRequest();
var rotationMap = {};
var angle = null;
var gui = null;
var blendmodes = [];
var stroketypes = [];
const seq = new SEQ();
const builtInSeq = new BUILTINSEQ();
const sageRequest = new SAGEREQUEST();
const rotationMapInput = new ROTATIONMAPINPUT();
const coord = new COORD();  
const config = new CONFIG();
//


//Process sage response
xhr.onload = function() {
    data = JSON.parse( xhr.response );
    convertedList = JSON.parse(data.stdout);
    if(seq.mod > 0){
        for(i = 0; i < convertedList.length; i++){
            convertedList[i] = convertedList[i] % seq.mod;
        }
    }
    seq.list = convertedList;
};
//Load GUI
window.onload = function() {
    loadInterface(null);
}

////////////////////////////////


//Draws one step
function stepDraw(angle, unitStep){
    console.log(angle);
    oldX = coord.X;
    oldY = coord.Y;
    coord.orientation = (coord.orientation + angle);
    coord.X += unitStep*Math.cos(coord.orientation);
    coord.Y += unitStep*Math.sin(coord.orientation);
    line(oldX,oldY,coord.X,coord.Y);
}

//Setup
p5.disableFriendlyErrors = true;
function setup(){
    blendmodes = [BLEND, DARKEST, LIGHTEST, DIFFERENCE, MULTIPLY, EXCLUSION, SCREEN, REPLACE, OVERLAY, HARD_LIGHT, SOFT_LIGHT, DODGE, BURN, ADD, NORMAL]
    stroketypes = [ROUND, SQUARE, PROJECT];
    config.blendMode = BLEND;
    config.strokeCap = ROUND;
    config.renderer = P2D;
    config.reset();
    coord.reset();
    rotationMapInput.apply();
    coord.i = -1;
    smooth();
    noLoop();
}

function draw(){
    if( (angle = rotationMap[seq.list[coord.i++]]) != undefined){
        stepDraw(angle, config.unitStep);
    }
    else{
        noLoop();
    }
}


/*
JSON.stringify(gui.load)
"{\"preset\":\"Default\",\"remembered\":{\"Default\":{\"0\":{},\"1\":{\"canvasHeight\":800,\"canvasWidth\":800,\"bgColor\":\"#000064\",\"lineColor\":\"#b8b8b8\",\"lineWeight\":2,\"unitStep\":20,\"strokeCap\":\"round\",\"blendMode\":\"source-over\"},\"2\":{\"sloaneID\":\"A000045\",\"count\":9000,\"mod\":5},\"3\":{\"keys\":\"0,1,2,3,4\",\"vals\":\"10,-20,-50,-71,60\"},\"4\":{\"initX\":300,\"initY\":400,\"initOrientation\":0}}}}"
JSON.stringify(gui.getSaveObject())
"{\"preset\":\"Default\",\"remembered\":{\"Default\":{\"0\":{},\"1\":{\"canvasHeight\":800,\"canvasWidth\":800,\"bgColor\":\"#000064\",\"lineColor\":\"#b8b8b8\",\"lineWeight\":2,\"unitStep\":20,\"strokeCap\":\"round\",\"blendMode\":\"source-over\"},\"2\":{\"sloaneID\":\"A000045\",\"count\":9000,\"mod\":5},\"3\":{\"keys\":\"0,1,2,3,4\",\"vals\":\"10,-20,-50,-71,60\"},\"4\":{\"initX\":300,\"initY\":400,\"initOrientation\":0}}},\"closed\":false,\"folders\":{\"controller\":{\"preset\":\"Default\",\"closed\":true,\"folders\":{}},\"Settings\":{\"preset\":\"Default\",\"closed\":true,\"folders\":{}},\"Sequence\":{\"preset\":\"Default\",\"closed\":false,\"folders\":{}},\"Rotation Map\":{\"preset\":\"Default\",\"closed\":false,\"folders\":{}},\"Coordinates\":{\"preset\":\"Default\",\"closed\":false,\"folders\":{}}}}"
*/