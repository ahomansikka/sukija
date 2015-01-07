/*
Copyright (@) 2008-2012, 2015 Hannu Väisänen (Firstname.Lastname@uef.fi)

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
%%
%class HVTokenizerImpl
%unicode
%integer
%pack
%char
%public
%final

%{
private static final Logger LOG = LoggerFactory.getLogger (HVTokenizerImpl.class);

public final int yychar()
{
  return yychar;
}


/**
 * Fill Lucene token with the current token text.
 */
public final void getText (org.apache.lucene.analysis.tokenattributes.CharTermAttribute t)
{
  t.copyBuffer (zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
}
%}

LETTER = [:letter:]+
DIGIT  = [:digit:]+
WHITESPACE = \r\n | [ \r\n\t\f]
NUM = {DIGIT}([-.,:_/]+{DIGIT})*(:{LETTER})?

// Definitions for LaTeX files.
T = ("\\"([~\^`'\"=.][A-Za-z]|[uvHcdbr]\{[A-Za-z]\}|t\{[A-Za-z][A-Za-z]\}))+

W = ({DIGIT}|{LETTER}|{T})

E = {W}(("\"-"|"''-"|"\\-"|"'-"|"--"|".-"|"-"|[.:']){W})*

F = ("["{LETTER}"]")?({E}("["{LETTER}"]")?)+

LPAR = "{"[0-9a-zA-Z]+"}"

%%

{NUM} {
  if (LOG.isDebugEnabled()) LOG.debug ("NUM [" + yytext() + "]");
} /* Ignore numbers. */



/* Ignore LaTeX commands. */

"\\"(documentclass|usepackage)("["[0-9a-zA-Z,]+"]")?{LPAR} {
  if (LOG.isDebugEnabled()) LOG.debug ("a [" + yytext() + "]");
}

"\\"(begin|end){LPAR} {
  if (LOG.isDebugEnabled()) LOG.debug ("b [" + yytext() + "]");
}

[\\][a-zA-Z@]+[*]? {
  if (LOG.isDebugEnabled()) LOG.debug ("c [" + yytext() + "]");
}


{F} {return 1;}

. | {WHITESPACE}   {} /* Ignore the rest. */
