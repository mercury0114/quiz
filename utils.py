from math import sqrt
from os.path import isfile
from random import choice

HINT = "h"
QUIT = "q"
INITIAL_SCORE = 5
GOOD_SCORE = 10
MAX_SCORE = 15
NEXT_QUESTION_INDEX = -1

def CloseTo(user_input, answer):
    l = min(len(user_input), len(answer))
    return sum(user_input[i] != answer[i] for i in range(l)) + \
        abs(len(user_input) - len(answer)) < 3

def GetFileScore(f):
    scores = ReadDataFromFile(f).values()
    score_sum = sum([sqrt(s[0]) + sqrt(s[1]) for s in scores])
    return (score_sum / (len(scores) * 2)) ** 2

def SelectQuestionAnswer(word_pair, argument):
    question_index = choice([0, 1]) if argument == 2 else argument
    return word_pair[question_index], word_pair[not question_index]

def WriteOpen(file_path):
    return open(file_path, "w", encoding='UTF-8')

def ReadOpen(file_path):
    return open(file_path, encoding='UTF-8')

def ReadDataFromFile(file_path, read_all_words=True):
    statistics = {}
    for line in ReadOpen(file_path):
        columns = line.split(', ')
        words = (columns[0], columns[1].strip('\n'))
        score1 = INITIAL_SCORE if len(columns) < 3 else int(columns[2])
        score2 = INITIAL_SCORE if len(columns) < 4 else int(columns[3])
        if read_all_words or score1 <= GOOD_SCORE or score2 <= GOOD_SCORE:
            statistics[words] = [score1, score2]
    return statistics

def ReadPairsFromFile(file_path):
    return list(ReadDataFromFile(file_path).keys())
