/*
Copyright (©) 2008-2012, 2015-2018, 2020 Hannu Väisänen

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

WHITESPACE = \r\n | [ \r\n\t\f]
DIGIT = [0-9]+
NUM = {DIGIT}([-.,:_/]+{DIGIT})*(:{ALPHANUM})?
HYPHEN = ("\"-"|"''-"|"\\-"|"'-"|"--"|".-"|"-")
PUNCT = [.:']

W1 = ({ALPHANUM})({PUNCT}({ALPHANUM}))*

W2 = {W1}(("["{ALPHANUM}"]")({W1})*)+ | (("["{ALPHANUM}"]"){W1})+("["{ALPHANUM}"]")?


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
  return Constants.BRACKET;
}


{V1} {
//  System.out.println ("V1 {" + yytext() + "}");
  if (yytext().indexOf ("\\-") > 0)
    return (Constants.HYPHEN + Constants.DASH);
  else
    return Constants.HYPHEN;
}

{V2} {
//  System.out.println ("V2 {" + yytext() + "}");
   if (yytext().indexOf ("\\-") > 0)
     return (Constants.HYPHEN + Constants.BRACKET +  Constants.DASH);
   else
     return (Constants.HYPHEN + Constants.BRACKET);
}


. | {WHITESPACE}   {} /* Ignore the rest. */
