from time import time
from sys import argv
from random import randint
from utils import ReadPairsFromFile, SelectQuestionAnswer
from utils import HINT, QUIT

ADD_COUNT = 2

def CloseTo(user_input, answer):
	if len(user_input) != len(answer):
		return False
	return sum(user_input[i] != answer[i] for i in range(len(answer))) < 2

if (len(argv) > 3 or len(argv) == 1):
    print("usage:")
    print("to ask questions from both columns:")
    print("python3 main.py [words_text_file]")
    print("to ask questions only from the 0-th column:")
    print("python3 main.py [words_text_file] 0")
    print("to ask questions only from the 1-st column:")
    print("python3 main.py [words_text_file] 1")
    exit()

column_argument = 2 if len(argv) == 2 else int(argv[2])
word_pairs = ADD_COUNT * ReadPairsFromFile(argv[1])
start_time = time()
mistakes_count = 0

print("Press {} for hint, {} for quit".format(HINT, QUIT))
while (word_pairs):
	index = randint(0, len(word_pairs)-1)
	question, answer = SelectQuestionAnswer(word_pairs[index], column_argument)
	print(question, "?")
	user_input = input()
	close_to_answer = CloseTo(user_input, answer)
	while user_input not in [HINT, QUIT] and not close_to_answer:
		print("Wrong answer, try again")
		mistakes_count += 1
		word_pairs += ADD_COUNT * [word_pairs[index]]
		user_input = input()
		close_to_answer = CloseTo(user_input, answer);
	if user_input == QUIT:
		print("Early exit, {} mistakes so far".format(mistakes_count))
		exit()
	if user_input == HINT:
		print(answer)
		mistakes_count += 1
		word_pairs += ADD_COUNT * [word_pairs[index]]
	if close_to_answer:
		word_pairs.pop(index)
		if user_input != answer:
			print("Close, correct answer is {}".format(answer))
		print("{} questions remain\n".format(len(word_pairs)))
    
print("You completed in {} seconds".format(int(time() - start_time)))
print("Mistakes/hints count: {}".format(mistakes_count))
