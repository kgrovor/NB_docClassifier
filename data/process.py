#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov  9 22:50:35 2017

@author: kshitij
"""
from remove import stopwords
file = open('train/labeledBow.txt','r')
doc = file.read().splitlines()
removals = []
#print(doc[0])
for i in range(len(doc)):
    ind = doc[i].split()
    rating = ind[0]
    removals = []
    for j in range(len(ind[1:])):
        if (int(ind[j].split(':')[0])) in stopwords:
            #del ind[j]
            #print("yaya")
            #print((int(ind[j].split(':')[0])))
            removals.append(j)
    for j in sorted(removals,reverse=True):
        del ind[j]
        
    doc[i] = rating + " " + ' '.join(ind)
        
doc = '\n'.join(doc)
