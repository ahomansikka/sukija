/*
Copyright (©) 2008-2012, 2015-2018, 2020, 2025 Hannu Väisänen

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
%state SKIP

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

private int nbraces = 0;
private int nbrackets = 0;
%}


// Ei toimi kuin UTF-8:ssa!
// ALPHANUM = [0-9A-Za-zÀ-ÖØ-öø-ÿ\u0100-\u017F]+

// A-Za-Z:        C0 Controls and Basic Latin
// À-ÖØ-öø-ÿ:     C1 Controls and Latin-1 Supplement (\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF)
// \u0100-\u017F: Latin Extended-A

ALPHANUM = [0-9A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u017F]+

// Abc:n, Bordeaux'iin, ev.-lut.
SEPARATOR = ([:']|".-")

WORD = {ALPHANUM}({SEPARATOR}{ALPHANUM})*

// Linja-auto.
COMPOUND_WORD = {WORD}("-"{WORD})+

WHITESPACE = \r\n | [ \r\n\t\f]
DIGIT = [0-9]+
NUM = {DIGIT}([-.,:_/]+{DIGIT})*(:{ALPHANUM})?

LATEX_COMPOUND_WORD = ("\"-"|"''-"|"'-"|"--")  // Eri tapoja kirjoittaa yhdysviiva LaTeXissa.
LATEX_HYPHEN = "\\-"   // Ta\-vu\-tus.

X0 = "["{ALPHANUM}"]"
X1 = ({LATEX_COMPOUND_WORD}|{SEPARATOR}|{LATEX_HYPHEN}|"-")
XX = ({X0}|{WORD})(({X0}|{WORD}|{X1})*({X0}|{WORD}))?

LPAR = "{"[0-9a-zA-Z]+"}"


%%

// Jäsennetään LaTeX-komentojen argumentit: lopeteaan, kun alku- ja loppusulkujen {} tai [] määrä on yhtä suuri.
//
<SKIP>{
"}{"  {}
"]["  {}
"{"   {nbraces++;}
"}["  {nbraces--; nbrackets++;}
"]{"  {nbraces++; nbrackets--;}
"}"   {nbraces--; if (nbraces == 0 && nbrackets == 0) yybegin(YYINITIAL);}
"["   {nbrackets++;}
"]"   {nbrackets--; if (nbraces == 0 && nbrackets == 0) yybegin(YYINITIAL);}
[^]   {if (nbraces == 0 && nbrackets == 0) yybegin(YYINITIAL);}
}


<YYINITIAL>{

"%\\bibtex{" ~ "}"        {}
"%\\kuvahakemisto{" ~ "}" {}
"%\\kuvatiedosto{" ~ "}"  {}

{NUM} {
  if (LOG.isDebugEnabled()) LOG.debug ("NR [" + yytext() + "]");
} /* Ignore numbers. */



/* Ignore some LaTeX commands. */


"\\"(begin|documentclass|emoji|end|figure|usepackage) {
  if (LOG.isDebugEnabled()) LOG.debug ("L0 [" + yytext() + "]");
  yybegin(SKIP);
}


/*
"\\"(documentclass|usepackage)("["[0-9a-zA-Z,]+"]")?{LPAR} {
  if (LOG.isDebugEnabled()) LOG.debug ("L1 [" + yytext() + "]");
}

"\\"(begin|end){LPAR} {
  if (LOG.isDebugEnabled()) LOG.debug ("L2 [" + yytext() + "]");
}
*/

// Jos rivinvaihtoa seuraa sana: "\\Esim".
// Ilman tätä jäsennetään LaTeX-komento "\Esim".
[\\][\\] {}

//[\\][a-zA-Z@]+[*]? {

[\\][\p{Letter}\p{Digit}@]+[*]? {
  if (LOG.isDebugEnabled()) LOG.debug ("L3 [" + yytext() + "]");
}


// Tavallinen sana, jolle ei tarvitse tehdä mitään jälkeenpäin.
{WORD} {
  if (LOG.isDebugEnabled()) LOG.debug ("W1 [" + yytext() + "]");
//  System.out.println ("W1 " +  yytext());
  return Constants.WORD;
}

{COMPOUND_WORD} {
  if (LOG.isDebugEnabled()) LOG.debug ("W2 [" + yytext() + "]");
//  System.out.println ("W2 " +  yytext());
  return Constants.COMPOUND_WORD;
}


// Sana, jota pitää käsitellä jälkeenpäin.
{XX} {
  if (LOG.isDebugEnabled()) LOG.debug ("XX [" + yytext() + "]");
  int what = 0;
  if (yytext().indexOf ('[') > -1) what += Constants.BRACKET;
  if (Constants.RE_LATEX_HYPHEN.matcher(yytext()).find()) what += Constants.LATEX_HYPHEN;
  if (Constants.RE_LATEX_COMPOUND_WORD.matcher(yytext()).find()) what += (Constants.LATEX_COMPOUND_WORD + Constants.COMPOUND_WORD);
/*
  System.out.println ("XX " + yytext() + " " + what + " " + (yytext().indexOf('[') > -1)
                      + " " + Constants.RE_LATEX_HYPHEN.matcher(yytext()).find()
                      + " " + Constants.RE_LATEX_COMPOUND_WORD.matcher(yytext()).find()
  );
*/

  return what;
}


. | {WHITESPACE}   {} /* Ignore the rest. */
}
