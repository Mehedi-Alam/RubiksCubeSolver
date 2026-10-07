import { useState } from 'react'
import './App.css'

function App() {

  // set up state variables to remember data on screen

//const [variableName, setterMethod] = useState(initialValue)
//setterMethod = function to update value: setTurns(newValue)
  const [turns, setTurns] = useState(5)
  const [scramble, setScramble] = useState([])
  const [solution, setSolution] = useState(null)

  // used to change button text to prevent spam
  const [isLoading, setIsLoading] = useState(false)

  const [showMoves, setShowMoves] = useState(false);
  const moves_list = ["F", "F'", "B", "B'", "L", "L'", "R", "R'",
            "U", "U'", "D", "D'", "F2", "B2", "L2", "R2", "U2", "D2"];
  
  // array representing cube
  const [stickers, setStickers] = useState([
    ...Array(9).fill(0),  // up
    ...Array(9).fill(1),  // left
    ...Array(9).fill(2),  // front
    ...Array(9).fill(3),  // right
    ...Array(9).fill(4),  // back
    ...Array(9).fill(5),  // down
  ]);

  // paintbrush to colour in cube 
  const [activeColour, setActiveColour] = useState(0);

  // mapping each colour to numbers 0-5
  const colourMap = {
    0: '#ffffff',
    1: '#ff8800',
    2: '#00d800',
    3: '#ff0000',
    4: '#0000ff',
    5: '#ffff00',
  };

  const handleSquareClick = (index) => {
    const newStickers = [...stickers];  // copy array 
    newStickers[index] = activeColour;  // put integer into array
    setStickers(newStickers); // update array
  };

  // function to draw a 3x3 face
  const Face = ({startIndex}) => (
    <div className="cube-face">
      {[0,1,2,3,4,5,6,7,8].map(offset => {
        const actualIndex = startIndex + offset;

        return (

          <button
            key={actualIndex}
            className="sticker"
            style={{backgroundColor: colourMap[stickers[actualIndex]] }}
            onClick={() => handleSquareClick(actualIndex)}
          />
        );
      })}
    </div>
  );


  // function to add moves to scramble array
  const handleManualMove = (move) => {
    setScramble(prevScramble => [...prevScramble,move]);
  }

  const handleCustomSolve = async () => {
    
    if (scramble.length == 0) {
      alert("Please enter a scramble first");
      return;
    }

    setIsLoading(true);

    try {

      const response = await fetch(`http://localhost:8080/api/solve-custom`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },

        // turn javascript array into JSON string 
        body: JSON.stringify({sequence: scramble})
      });

    const data = await response.json();

    setScramble(data.scramble);
    setSolution(data.solution);

  } catch (error) {
    console.error("Error connecting to the java server:", error);
    alert("Could not connect to the server.")
  } finally {
    setIsLoading(false);
  }
};

  // function that talks to java
  // called when solve button is clicked
  const handleSolve = async () => {

    // change button text and disabled clicking 
    setIsLoading(true) 

    try {
      // call sprint boot API running on port 8080
      // spring boot runs solve                                    pass turns input
      const response = await fetch(`http://localhost:8080/api/solve?turns=${turns}`)

      // convert the JSON response into a javascript object
      const data = await response.json()

      // update react screen with fresh data from recieved arrays in 'data'
      setScramble(data.scramble)
      setSolution(data.solution);

    } catch (error) {
      console.error("Error connecting to the java server:", error)
      alert("Could not connect to server.")
    } finally { // turn button back initial state
      setIsLoading(false)
    }
  }

  const handleColouredSolve = async () => {

    // input validation
    const colourCounts = [0, 0, 0, 0, 0, 0];

    // count how many of each colour exist on board
    stickers.forEach(colourInt => {
      colourCounts[colourInt]++;
    });

    // check if any colour does not have 9 stickers
    const isInvalid = colourCounts.some(count => count !== 9);

    if (isInvalid) {
      alert("Invalid cube, there must be exactly 9 squares of each colour.");
      return; // stop function
    }
    // ----

    setIsLoading(true);

    try {
      const response = await fetch(`http://localhost:8080/api/solve-coloured`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        // package the 54 integers into object with key=state
        body: JSON.stringify({state: stickers})
      });

      const data = await response.json();

      // upate solution text box
      setSolution(data.solution);

      // clear scramble box
      setScramble([])

    } catch (error) {
        console.error("Error connecting to the java server:", error)
        alert("Could not connect to server.")
    } finally { // turn button back initial state
      setIsLoading(false)
    }
  };

  const handleResetCube = () => {

    if (window.confirm("Are you sure want to clear the cube?")) {
      setStickers([
        ...Array(9).fill(0), // up
        ...Array(9).fill(1), // left
        ...Array(9).fill(2), // front
        ...Array(9).fill(3), // right
        ...Array(9).fill(4), // back
        ...Array(9).fill(5), // down
      ]);

      setSolution(null);
    }
  };

  // html for UI
  return (
    <div className="app-container">
      <h1>Rubiks Cube Solver</h1>

      {/* scramble by turns num*/}
      {/* <div className="input-section">
        <label>
          <strong>Scramble Length: </strong>
          <input
            type='number'
            value={turns} // whatever is in turns variable goes in input box
            onChange={(e) => setTurns(e.target.value)}  // updates variable when inputted
            min="1"
            max="20"
            className="turns-input"
          ></input>
        </label>
      </div> */}

      <button 
        className="show-moves-button"
      
        onClick={() => setShowMoves(!showMoves)}>
        
        {showMoves ? 'Hide Moves' : 'Show Moves'}
      </button>
        
      {showMoves && (
        <div style={{marginBottom: '20px'}}>

          <div className="container">
                    
            {/* loop through moves_list and make button for each move*/}
            {moves_list.map((move) => (
              <button
                key={move}
                onClick={() => handleManualMove(move)}
              >
                {move}
              </button>
            ))}
          </div>

          {/* display inputted moves*/}
          <div style={{marginTop: '15px'}}>
            <strong>Moves Sequence</strong>
            <span className="moves-sequence-list">
              {scramble.length > 0 ? scramble.join('') : "Click buttons to make scramble"}
            </span>

            {scramble.length > 0 && (
              <>  
                <button
                  onClick={() => {
                    setScramble([]);
                    setSolution(null);
                  }}
                  className="clear-button"
                >
                  Clear
                  </button>

                  <button
                    onClick={handleCustomSolve}
                    disabled={isLoading}
                    className="solve-button"
                    style={{marginLeft: '10px'}}
                  >
                    {isLoading ? "Solving..." : "Solve"}
                  </button>
              </>
            )}
          </div>
      
        </div>
      )}
      
      <button 
      onClick={handleSolve} // call handleSolve
      disabled={isLoading}
      className="solve-button"
      >
        {isLoading ? 'Solving...' : 'Solve'}
      </button>

      <hr style={{ margin: '30px 0'}}></hr>

      {/* only show these boxes if we have data from server */}
      {solution !== null && (
        <div>

          {/* only show this if a scramble exists */}
          {scramble.length > 0 && (
            <>
              <h2>Scramble Moves:</h2>
              <p className="results-box scramble-box">
                {scramble.join(' ')}
              </p>
            </>
          )}

          {/* always show solution */}
          <h2>Solution:</h2>
          <p className="result-box solution-box">
            {solution.length === 0? "Already Solved" : solution.join(' ')}
          </p>
        </div>
      )}

      {/* colour selector */}
      <div className="colour-selector">
        <strong>Select Colour: </strong>
        {[0,1,2,3,4,5].map(colourInt => (

          <button
          key={colourInt}
          onClick={() => setActiveColour(colourInt)}

          style={{
            backgroundColor: colourMap[colourInt],
            border: activeColour === colourInt ? '3px solid black' : '1px solid gray',
          }}
          />
        ))}
      </div>

      {/* unfolded cube */}
      <div className="unfolded-cube">

        {/* row 1 (up)*/}
        <div style={{gridColumn: 2}}> <Face startIndex={0}/> </div>

        {/* row 2 (left, front, right, back) */}
        <div style={{gridColumn: 1}}> <Face startIndex={9}/> </div>
        <div style={{gridColumn: 2}}> <Face startIndex={18}/> </div>
        <div style={{gridColumn: 3}}> <Face startIndex={27}/> </div>
        <div style={{gridColumn: 4}}> <Face startIndex={36}/> </div>

        {/* row 3 (down) */}
        <div style={{gridColumn: 2}}> <Face startIndex={45}/> </div>
      </div>

      {/* solve button for coloured cube*/}
      <div style={{textAlign: 'center', marginTop: '20px'}}>

        {/* cube reset button */}
        <button
          onClick={handleResetCube}
          className='reset-button'
          style={{ marginRight: '15px' }}
        >
          Reset Cube
        </button>

        <button
          onClick={handleColouredSolve}
          disabled={isLoading}
          className='solve-button'
        >
          {isLoading ? 'Solving...' : 'Solve Coloured Cube'}
        </button>
      </div>

    </div>
  )
}

export default App

// TODO: reset button for cube layout, input validation for layout cube