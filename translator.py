# Words are decapitalised and have digits/punctuation characters removed.
# Start the program by typing:
# python3 translator.py [script_file_path] [optional_language_tag]
# The output will be stored in the file "all_words.txt"
from sys import argv
from string import punctuation
from multiprocessing.dummy import Pool
from google_trans_new import google_translator
from google_trans_new.google_trans_new import google_new_transError
from utils import read_open, write_open

OUTPUT_FILE = "all_words.txt"


def ReadWordsAndCount(file_name):
    counts = {}
    for line in read_open(file_name):
        for word in line.split():
            word = ''.join(ch for ch in word if ch not in punctuation)
            word = word.lower()
            if word and not any(char.isdigit() for char in word):
                counts.setdefault(word, 0)
                counts[word] += 1
    return sorted(counts, key=counts.get, reverse=True), counts


def Translate(word):
    language = "en" if len(argv) <= 2 else argv[2]
    return google_translator(timeout=5).translate(word, lang_src=language)


print("Reading words from the input file...")
words, counts = ReadWordsAndCount(argv[1])

print("Translating all words, please wait...")
pool = Pool(10)
try:
    translations = pool.map(Translate, words)
except google_new_transError:
    print("Too many words to translate, outputing them without translation")
    translations = words

print("Writing translations to {}...".format(OUTPUT_FILE))
current_count = None
f = write_open(OUTPUT_FILE)
for word, translation in zip(words, translations):
    if current_count != counts[word]:
        current_count = counts[word]
        f.write("Words that occured {} times:\n".format(current_count))
    f.write("{}, {}\n".format(word, translation))
print("Script finished")
