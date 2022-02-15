from time import time
from sys import argv
from random import randint
from utils import CloseTo, ReadPairsFromFile, SelectQuestionAnswer
from utils import HINT, QUIT, NEXT_QUESTION_INDEX

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
word_pairs = 2 * ReadPairsFromFile(argv[1])
start_time = time()
mistakes_count = 0
index = NEXT_QUESTION_INDEX

print("Press {} for hint, {} for quit".format(HINT, QUIT))
while (word_pairs):
	if index == NEXT_QUESTION_INDEX:
		index = randint(0, len(word_pairs)-1) 
		question, answer = SelectQuestionAnswer(word_pairs[index], column_argument)
	print(question, "?")
	user_input = input()
	while user_input not in [HINT, QUIT, answer]:
		if CloseTo(user_input, answer):
			print("Close, try again")
		else:
			print("Wrong, try again")
			mistakes_count += 1
			word_pairs += [word_pairs[index]]
		user_input = input()
	if user_input == QUIT:
		print("Early exit, {} mistakes so far".format(mistakes_count))
		exit()
	if user_input == HINT:
		print(answer)
		mistakes_count += 1
		word_pairs += 3 * [word_pairs[index]]
	if user_input == answer:
		word_pairs.pop(index)
		print("{} questions remain\n".format(len(word_pairs)))
		index = NEXT_QUESTION_INDEX
print("You completed in {} seconds".format(int(time() - start_time)))
print("Mistakes/hints count: {}".format(mistakes_count))
