from sys import argv
from os import listdir
from os.path import join, basename
from random import choice
from math import ceil, sqrt
from utils import CloseTo, ReadOpen, ReadPairsFromFile, WriteOpen
from utils import HINT, QUIT

STATISTICS_FILE = "my.stats"
MAX_SCORE = 30
INITIAL_SCORE = 5
SAME_FILE_COUNT = 12

# Each triple is (language1_phrase, language2_phrase, file_name)
def ReadTriplesFromFolder(folder):
	triples = []
	for f in listdir(folder):
		if f == STATISTICS_FILE: continue
		pairs = ReadPairsFromFile(join(folder, f))
		triples += [(pair[0], pair[1], f) for pair in pairs]
		triples += [(pair[1], pair[0], f) for pair in pairs]
	return triples

def GetStatistics(triples, folder):
	statistics = {triple : INITIAL_SCORE for triple in triples}
	if STATISTICS_FILE not in listdir(folder): return statistics
	for line in ReadOpen(join(folder,STATISTICS_FILE)):
		word1, word2, file_name, score = line.split(', ')
		if (word1, word2, file_name) in statistics:
			statistics[(word1, word2, file_name)] = int(score)
	return statistics

def WriteStatistics(statistics, folder):
	output = WriteOpen(join(folder, STATISTICS_FILE))
	sorted_triples = sorted(statistics, key=statistics.get)
	for t in sorted_triples:
		output.write("{}, {}, {}, {}\n".format(t[0], t[1], t[2], statistics[t]))
    
if len(argv) != 2:
	print("usage:")
	print("python3 folder_checker.py [folder]")
	exit()

folder = argv[1]
triples = ReadTriplesFromFolder(folder)
statistics = GetStatistics(triples, folder)

print("Press {} for hint, {} to quit".format(HINT, QUIT))
file_name = None
same_file_count = 0
while True:
	min_score = min(statistics.values())
	if same_file_count == 0:
		WriteStatistics(statistics, folder)
		print("CHOOSING NEW FILE\n");
		file_name = choice([t for t in statistics if statistics[t] == min_score])[2]
		same_file_count = SAME_FILE_COUNT
	triples = [t for t in statistics if t[2] == file_name]
	min_score = min([statistics[t] for t in triples])
	triple = choice([t for t in triples if statistics[t] <= min_score * 1.5])
	question, answer = triple[0], triple[1]
	score = statistics[triple]
	print("{} more from {}".format(same_file_count, triple[2]))
	print("Current score is", score)
	print(question)
	user_input = input()
	while user_input not in [answer, HINT, QUIT]:
		if CloseTo(user_input, answer):
			print("Close, try again")
		else:
			print("Wrong answer, try again")
			score = max(0, score - 2)
			same_file_count += 1
		user_input = input()
	if user_input == answer:
		score = min(MAX_SCORE, score + 1)
		same_file_count -= 1
	if user_input == HINT:
		print(answer)
		score -= max(0, score // 2)
		same_file_count += 2
	if user_input == QUIT:
		exit()
	print("New score is {}\n".format(score))
	statistics[triple] = score
