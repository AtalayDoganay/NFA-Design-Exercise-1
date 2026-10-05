"""Independent finite-case verifier; uses Python standard library only."""
from pathlib import Path
from itertools import product
import xml.etree.ElementTree as ET
import json

ROOT = Path(__file__).resolve().parents[1]

def load_machine(path):
    root = ET.parse(path).getroot()
    assert root.findtext('type') == 'fa'
    machine = root.find('automaton')
    ids = {s.attrib['id'] for s in machine.findall('state')}
    starts = [s.attrib['id'] for s in machine.findall('state') if s.find('initial') is not None]
    assert len(starts) == 1
    finals = {s.attrib['id'] for s in machine.findall('state') if s.find('final') is not None}
    edges = {}
    for t in machine.findall('transition'):
        a,b,c = t.findtext('from'),t.findtext('to'),t.findtext('read') or ''
        assert a in ids and b in ids and c in ('','0','1')
        edges.setdefault((a,c),set()).add(b)
    return starts[0],finals,edges

def accepts(machine,s):
    start,finals,edges = machine
    def close(states):
        states=set(states);todo=list(states)
        while todo:
            q=todo.pop()
            for dest in edges.get((q,''),set()):
                if dest not in states: states.add(dest);todo.append(dest)
        return states
    active=close({start})
    for c in s:
        active=close({dest for q in active for dest in edges.get((q,c),set())})
    return bool(active & finals)

def expected(n,s):
    if any(c not in '01' for c in s): return False
    if n==7: return s.endswith('10')
    if n==8: return s.startswith('01') and s.endswith('10')
    if n==11: return len(s)>=2 and s[-2]=='1'
    if n==23: return len(s)%2==1 or s=='01'
    if n==24: return s.count('0')%2==1 or s=='001'
    raise ValueError(n)

def main():
    manifest=json.loads((ROOT/'tools'/'expected.json').read_text())
    lines=['NFA validation — independent Python state-set simulator',
           'This file records independent Python checks. Actual JFLAP results are in jflap-validation.txt.',
           'Inputs tested exhaustively: all binary strings of length 0 through 12.', '']
    total=0
    for n in (7,8,11,23,24):
        machine=load_machine(ROOT/f'n{n:02d}.jff')
        tokens=(ROOT/f'n{n:02d}t.txt').read_text().split()
        assert len(tokens)==16 and len(set(tokens))==16
        flags=[]
        for s in tokens:
            assert all(c in '01' for c in s)
            want=expected(n,s);flags.append(want)
            assert want==manifest[str(n)]['expected'][s]
            assert accepts(machine,s)==want,(n,s)
        assert flags==[True]*8+[False]*8,'Accepted tests must precede rejected tests'
        count=0
        for length in range(13):
            for bits in product('01',repeat=length):
                s=''.join(bits)
                assert accepts(machine,s)==expected(n,s),(n,s,'language mismatch')
                count+=1
        for s in ['2','a','010x','x001']:
            assert not accepts(machine,s)
        total+=count
        lines.append(f'n{n:02d}: PASS — {count:,} exhaustive binary cases; 16 ordered file tests; empty input Reject; 4 nonbinary checks.')
    lines.extend(['',f'Total: {total:,} exhaustive binary cases passed.',
                  'Finite testing is not a proof for all lengths; read the construction explanation in each report.',
                  'JFLAP GUI screenshots and results are recorded separately in the problem reports.'])
    output='\n'.join(lines)+'\n'
    (ROOT/'validation.txt').write_text(output,encoding='utf-8')
    print(output)

if __name__=='__main__':main()
