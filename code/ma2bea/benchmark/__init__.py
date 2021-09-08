from .soo import *
from .matsoo import get_functions

def get_functions(args):
    if args.benchmark_id == 0:
        functions = [f1, f2, f3, f4, f5, f6, f7, f8, f9, f10]
    else:
        functions = matsoo.get_functions(args)
    return functions

