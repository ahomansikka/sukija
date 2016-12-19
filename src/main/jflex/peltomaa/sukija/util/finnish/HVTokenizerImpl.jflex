/*
Copyright (@) 2008-2012, 2015-2016 Hannu Väisänen (Firstname.Lastname@uef.fi)

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
import peltomaa.sukija.util.Constants;
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


// A-Za-Z:        C0 Controls and Basic Latin
// À-ÖØ-öø-ÿ:     C1 Controls and Latin-1 Supplement
// \u0100-\u017F: Latin Extended-A
// \u0180-\u0245: Latin Extended-B

// Ei käänny Windowsissa!
//LETTER_OR_DIGIT = [0-9A-Za-zÀ-ÖØ-öø-ÿ\u0100-\u017F\u0180-\u0245]+

LETTER = [:letter:]+
DIGIT  = [:digit:]+
LETTER_OR_DIGIT = ({LETTER}|{DIGIT})

DIGIT  = [0-9]+
WHITESPACE = \r\n | [ \r\n\t\f]
NUM = {DIGIT}([-.,:_/]+{DIGIT})*(:{LETTER_OR_DIGIT})?
HYPHEN = ("\"-"|"''-"|"\\-"|"'-"|"--"|".-"|"-")
PUNCT = [.:']

W1 = ({LETTER_OR_DIGIT})({PUNCT}({LETTER_OR_DIGIT}))*

W2 = {W1}(("["{LETTER_OR_DIGIT}"]")({W1})*)+ | (("["{LETTER_OR_DIGIT}"]"){W1})+("["{LETTER_OR_DIGIT}"]")?


LPAR = "{"[0-9a-zA-Z]+"}"

V1 = {W1}({HYPHEN}{W1})+
V2 = {W2}({HYPHEN}{W2})+

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


{W1} {
//  System.out.println ("W1 {" + yytext() + "}");
  return Constants.WORD;
}

{W2} {
//  System.out.println ("W2 {" + yytext() + "}");
  return (Constants.BRACKET);
}


{V1} {
//  System.out.println ("V1 {" + yytext() + "}");
  return Constants.HYPHEN;
}

{V2} {
//  System.out.println ("V2 {" + yytext() + "}");
  return (Constants.HYPHEN & Constants.BRACKET);
}


. | {WHITESPACE}   {} /* Ignore the rest. */
