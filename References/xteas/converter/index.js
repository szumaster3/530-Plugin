var fs = require('fs')
const path = require('path')

const IN_FOLDER_PATH = './in/'
const OUT_FOLDER_PATH = './out/'
const TXT_EXTENSION_LENGTH = ".txt".length


function getDirectories(srcpath) {
    return fs.readdirSync(srcpath)
      .map(file => path.join(srcpath, file))
      .filter(path => fs.statSync(path).isDirectory())
}


function readFiles(dirname, revision) {

    const data = new Array()
    const fileNames = fs.readdirSync(dirname)
    
    fileNames.forEach((fileName) => {
        const fileContent = fs.readFileSync(dirname + fileName, 'utf-8')
        
        const region = fileName.substring(0, fileName.length - TXT_EXTENSION_LENGTH)
        const keys = fileContent.split('\n').slice(0, 4)
        
        data.push({ region, keys })
    })

    
    const outDir = path.join(OUT_FOLDER_PATH, revision)
    const outPath = path.join(outDir, revision + '.json')
    
    if (!fs.existsSync(outDir)){
        fs.mkdirSync(outDir);
    }
    
    data.sort((a, b) => a.region - b.region)

    fs.writeFileSync(outPath, JSON.stringify(data, null, 4), 'utf8')
    console.log('Wrote ' + fileNames.length + ' regions to revision ' + revision + ' JSON file. (' + outPath + ')')
}




function main() {
    const revisionPaths = getDirectories(IN_FOLDER_PATH)
    revisionPaths.forEach((revPath) => {
        const splitPath = revPath.split('\\')
        const revision = splitPath[splitPath.length - 1]
        const data = readFiles(revPath + '/', revision)
    })
}

main()


/*
[
  {
    "region": 4662,
    "keys": [
      327206325,
      671317497,
      652416162,
      -982088476
    ]
  },
  {
    "region": 4663,
    "keys": [
      1565844634,
      -8570921,
      1979007770,
      112329484
    ]
  },
  */