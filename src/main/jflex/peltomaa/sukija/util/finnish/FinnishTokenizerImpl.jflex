/*
Copyright (©) 2008-2012, 2016-2017, 2020 Hannu Väisänen

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package peltomaa.sukija.finnish;

/**
 * Finnish tokenizer constructed with JFlex.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

%%
%class FinnishTokenizerImpl
%unicode
%integer
%pack
%char
%public
%final

%{
private static final Logger LOG = LoggerFactory.getLogger (FinnishTokenizerImpl.class);


public final int yychar()
{
  return (int)yychar;
}


/**
 * Fill Lucene token with the current token text.
 */
public final void getText (org.apache.lucene.analysis.tokenattributes.CharTermAttribute t)
{
  t.copyBuffer (zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
}
%}


// Ei toimi kuin UTF-8:ssa!
// ALPHANUM = [0-9A-Za-zÀ-ÖØ-öø-ÿ\u0100-\u017F]+

// A-Za-Z:        C0 Controls and Basic Latin
// À-ÖØ-öø-ÿ:     C1 Controls and Latin-1 Supplement (\u00C0-\u00D6\u00D8-\u00FF)
// \u0100-\u017F: Latin Extended-A

ALPHANUM = [0-9A-Za-z\u00C0-\u00D6\u00D8-\u00FF\u0100-\u017F]+

// Linja-auto, abc:n, Bordeaux'iin, ev.-lut.
SEPARATOR = ([-:']|".-")

WORD = {ALPHANUM}({SEPARATOR}{ALPHANUM})*

WHITESPACE = \r\n | [ \r\n\t\f]
%%

{WORD} {
  if (LOG.isDebugEnabled()) LOG.debug ("[" + yytext() + "]");
  return 1;
}

. | {WHITESPACE}   {} /* Ignore the rest. */
