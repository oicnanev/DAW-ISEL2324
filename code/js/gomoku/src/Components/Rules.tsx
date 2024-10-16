import * as React from 'react';
import { useEffect, useState } from 'react';


interface Rule { name: string, description: string }
interface Rules { generic: string; specific: SpecificRule; variants: VariantRule; openings: OpeningRule; }
interface SpecificRule { rule: Rule; }
interface VariantRule { rule: Rule; }
interface OpeningRule { rule: Rule | SoosorvRule; }
interface SoosorvRule { sequenceOfMoves: Rule; restrictionsOnFirstMove: Rule; }

export function Rules() {
  const [rules, setRules] = useState<Rules | undefined>(undefined);

  useEffect(() => {
    const fetchRules = async () => {
      try {
        const response = await fetch('/api/rules');
        const rules = await response.json();
        setRules(rules);
      } catch (error) {
        console.log(error);
        return (
          <>
            <div>Failed to fetch rules</div>
            <div>{error.message}</div>
          </>
        );
      }
    };
    fetchRules();
  }, []);


  const rulesStyle = { padding: '70px' };
  const textAreaStyle = { padding: '10px' };

  return (
    <main style={rulesStyle} role="main" className="container">
      <div className="card bg-dark text-white-50" style={textAreaStyle}>
        <h2><i className="bi bi-journal"></i> Rules</h2>
        <br />
        <div>
          <h4><i className="bi bi-dot"></i> Generic</h4>
          <p>{rules?.generic}</p>
          <hr/>
          <h4><i className="bi bi-dot"></i> Specific</h4>
          <ul>
            {Object.values(rules?.specific ?? {}).map((value, index) => (
              <li key={index}><b>{value.name}</b><br />{value.description}</li>
              )
            )}
          </ul>
          <hr/>
          <h4><i className="bi bi-dot"></i> Variants</h4>
          <ul>
            {Object.values(rules?.variants ?? {}).map((value, index) => (
              <li key={index}><b>{value.name}</b><br />{value.description}</li>
              )
            )}
          </ul>
          <hr/>
          <h4><i className="bi bi-dot"></i> Openings</h4>
          <ul>
            {Object.values(rules?.openings ?? {}).map((value, index) => (
              <li key={index}><b>{value.name}</b><br />{value.description}</li>
              )
            )}
          </ul>
          <hr/>
        </div>
      </div>
    </main>
  );
}