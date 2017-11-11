#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov  9 22:50:35 2017

@author: kshitij
"""
from list_stopwords import stopwords
file = open('train/labeledBow.txt','r')
doc = file.read().splitlines()
removals = []
for i in range(len(doc)):
    ind = doc[i].split()
    rating = ind[0]
    removals = []
    for j in range(len(ind[1:])):
        if (int(ind[j].split(':')[0])) in stopwords:
            removals.append(j)
    for j in sorted(removals,reverse=True):
        del ind[j]
        
    doc[i] = rating + " " + ' '.join(ind)
doc = '\n'.join(doc)
text_file = open("Output.txt", "w")
text_file.write(doc)
text_file.close()
