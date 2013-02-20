<?xml version='1.0' encoding='UTF-8'?>

<!--
Tämä on muokattu koodista
http://stackoverflow.com/questions/2411330/how-to-format-solr-result-documents
-->

<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output media-type="text/html; charset=UTF-8" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

<!-- Sivutuksen muuttujat. -->
<xsl:variable name="pageNumber" select="(response/result/@start div 10) + 1"/>
<xsl:variable name="numberOfRecords" select="response/result/@numFound"/>
<xsl:variable name="recordsPerPage" select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='rows']" />
<xsl:variable name="endPage" select="ceiling(response/result/@numFound div 10)" />
<xsl:variable name="cantPages" select="5" />

<!-- Etsitty sana. -->
<xsl:variable name="queryWord" select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" />

<xsl:key name="preg" match="lst[@name='highlighting']/lst" use="@name"/>

<xsl:template match="/">
<style>
  #seekResult em {
    font-weight: bold;
    font-style: normal;
  }
</style>


<table id="seekResult">
  <xsl:for-each select="response/result/doc">
  <tr>
    <td style="padding-bottom: 15px;">
      <div style="font-family: arial; font-size: 16px">
        <a>
          <xsl:attribute name="href"><xsl:value-of select="arr[@name='id']"/></xsl:attribute>
          <xsl:value-of select="arr[@name='id']"/>
        </a>
      </div>
      <div style="font-family: arial; color: #000000; font-size: 14px;">
        <xsl:apply-templates select="key('preg', ./arr[@name='id'])" mode="name"/>
      </div>
    </td>
  </tr>
  </xsl:for-each>
  <xsl:if test="not(response/result/doc)">
           <h4>Ei osumia.</h4>
  </xsl:if>

  <!-- Sivutus. -->
  <xsl:if test="response/result/doc">
    <tr>
      <td>
        <xsl:if test="$recordsPerPage &lt; $numberOfRecords">
          <xsl:if test="$pageNumber &gt; 1">
            <xsl:variable name="offset" select="10*($pageNumber - 2)"/>
            <span style="margin-right: 5px;"><a href="?q={$queryWord}&amp;version=2.2&amp;start={$offset}&amp;rows=10&amp;indent=on">&lt; Edellinen</a> |</span>
          </xsl:if>
          <xsl:call-template name="pageNumbers">
            <xsl:with-param name="current" select="$pageNumber"/>
            <xsl:with-param name="max">
              <xsl:choose>
                  <xsl:when test="(($pageNumber + $cantPages) &gt; $endPage) or ($endPage &lt;= 9)">
                      <xsl:value-of select="$endPage" />
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:value-of select="($pageNumber + $cantPages)" />
                  </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="number">
              <xsl:choose>
                <xsl:when test="(($pageNumber - $cantPages) &lt; 1) or ($endPage &lt;= 9)">
                  <xsl:value-of select="1" />
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="($pageNumber - $cantPages)" />
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:if test="(($pageNumber ) * $recordsPerPage) &lt; ($numberOfRecords)">
            <xsl:variable name="nextStart" select="10*($pageNumber)"/>
            <span style="margin-right: 5px;">| <a href="?q={$queryWord}&amp;version=2.2&amp;start={$nextStart}&amp;rows=10&amp;indent=on">Seuraava &gt;</a></span>
          </xsl:if>
          <span style="margin-right: 5px;">(Osumien määrä: <xsl:value-of select="$numberOfRecords"/>)</span>
        </xsl:if>
      </td>
    </tr>
  </xsl:if>
</table>
</xsl:template>


<xsl:template match="arr/str" mode="name">
  <xsl:value-of select="." disable-output-escaping="yes"/>
</xsl:template>



<xsl:template name="pageNumbers">
  <xsl:param name="current"/>
  <xsl:param name="number"/>
  <xsl:param name="max"/>

  <xsl:choose>
    <xsl:when test="$number = $current">
      <!-- Show current page without a link. -->
      <span class="current" style="margin-right: 5px;">
        <xsl:value-of select="$number"/>
      </span>
    </xsl:when>
    <xsl:otherwise>
      <span style="margin-right: 5px;">
        <a href="?q={$queryWord}&amp;version=2.2&amp;start={($number - 1) * $recordsPerPage}&amp;rows=10&amp;indent=on"><xsl:value-of select="$number"/></a>
      </span>
    </xsl:otherwise>
  </xsl:choose>

  <!-- Recursively call the template untill we reach the max number of pages. -->
  <xsl:if test="$number &lt; $max">
    <xsl:call-template name="pageNumbers">
      <xsl:with-param name="current" select="$current"/>
      <xsl:with-param name="number" select="$number+1"/>
      <xsl:with-param name="max" select="$max"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
