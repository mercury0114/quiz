from math import sqrt
from os.path import isfile
from random import choice

HINT = "h"
QUIT = "q"

def CloseTo(user_input, answer):
	l = min(len(user_input), len(answer))
	return sum(user_input[i] != answer[i] for i in range(l)) + \
		abs(len(user_input) - len(answer)) < 3

def GetFileScore(f):
	if not isfile(f): return 0
	count = 0
	score_sum = 0
	for line in ReadOpen(f):
		p1, p2, score = line.split(', ')
		count += 1
		score_sum += sqrt(int(score))
	return score_sum / count

def SelectQuestionAnswer(word_pair, argument):
    question_index = choice([0, 1]) if argument == 2 else argument
    return word_pair[question_index], word_pair[not question_index]

def WriteOpen(file_path):
    return open(file_path, "w", encoding='UTF-8')

def ReadOpen(file_path):
    return open(file_path, encoding='UTF-8')

def ReadPairsFromFile(file_path):
    pairs = []
    for line in ReadOpen(file_path):
        wordA, wordB = line.split(', ')
        pairs.append((wordA, wordB.strip('\n')))
    return pairs
