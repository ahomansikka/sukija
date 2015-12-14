/*
Copyright (C) 2015 Hannu Väisänen (Firstname.Lastname@uef.fi)

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

package peltomaa.sukija.voikko.tokenizer;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
%%
%class JFlexReader
%unicode
%integer
%pack
%char
%final

%{
//private static final Logger LOG = LoggerFactory.getLogger (HVTokenizerImpl.class);


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

INPUTCHAR = [^ \t\r\n]+
WHITESPACE = \r\n | [ \r\n\t\f]


%%

{INPUTCHAR} {
//  if (LOG.isDebugEnabled()) LOG.debug ("NUM [" + yytext() + "]");
//  System.out.println ("{" + yytext() + "}");
  return 0;
}


[ \r\n\t\f]"--"[ \r\n\t\f]   {} /* Ignore "--". */


. | {WHITESPACE}   {} /* Ignore the rest. */
