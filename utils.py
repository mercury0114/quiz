from random import choice

HINT = "h"
QUIT = "q"

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
