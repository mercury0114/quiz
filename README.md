**Description:** A simple project to examine my vocabulary. <p>&nbsp;</p>

**Example usage**: `python3 file_checker.py example_data/words.txt`

this will read words from the `words.txt` file and ask you to provide `English => German` or `German => English` translations.    
   
   If you need a hint, press `h`. Each hint or an incorrect answer costs you a penalty - the question will be asked later more times.
   
   Press `q` to quit. <p>&nbsp;</p>

**Custom usage**:

1) Look at the `example_words.txt` file, create your own
   file using the same format

2) Run the shell command `python3 file_checker.py [your_words_text_file] [NUMBER]`
   
   where `NUMBER` can be equal to:
       
   0, to ask questions only from the 0-th column
       
   1, to ask questions only from the 1-st column

   **Example command:** `python3 main.py example_data/words.txt 1`
   
   Command without the `NUMBER` argument will ask questions from both columns
   
 <p>&nbsp;</p>
   
**To run on Android phone**:

 1) Install `Pydroid` app
 2) Open Chrome browser app, in settings choose "Computer Version", navigate to github and download this project.
 3) Open `Pydroid` app and open the terminal. Navigate to downloaded git repo directory.
 4) Execute the program typing the regular `python3 file_checker.py example_data/words.txt` command.
 5) To conveniently edit `.txt` files in Android, install `txtpad` app.
