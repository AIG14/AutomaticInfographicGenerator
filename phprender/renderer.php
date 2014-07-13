<?php
/**
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

/** 
 * Render a title page.
 * @author Peter
 * @param $pdf the current instance of the PDF to append.
 * @param $title the page title.
 * @returns void
 */
function renderTitlePage($pdf, $title)
{
    renderPageWithbackground($pdf);
    $pageWidth = $pdf->GetPageWidth();
    $pdf->SetFont('helvetica', 'B', 60);
    $pdf->SetTextColor(255, 255, 254);
    $pdf->WriteHtmlCell($pageWidth, 100, 0, 130, $title, 0, 0, false, true, 'C');
}

/** 
 * Render a category set as a pie chart.
 * @author Peter
 * @param $pdf the current instance of the PDF to append.
 * @param $title the title content.
 * @param $content values to render.
 * @returns void
 */
function renderCategory($pdf, $title, $content)
{
    renderPageWithbackground($pdf);
    $pageWidth = $pdf->GetPageWidth();
    $pageHeight = $pdf->GetPageHeight();
    $total = 0;
    $numberOfItems = 0;
    $data = explode("\n", $content);
    foreach ($data as $dataLine) {
        if (strpos($dataLine, ':') !== false) {
            $dataRowParams = explode(":", $dataLine);
            $total += intval(trim($dataRowParams[1]));
            $numberOfItems++;
        }
    }

    if ($total == 0) {
        $total = 1;
    }

    $startLocation = 250;
    $colours = getFlatColours();
    $pdf->SetFont('helvetica', '', 12);
    $pdf->SetTextColor(255, 255, 255);
    $counter = 0;
    foreach ($data as $dataLine) {
        if (strpos($dataLine, ':') !== false) {
            $dataRowParams = explode(":", $dataLine);
            $widthDegrees = (intval(trim($dataRowParams[1])) / $total) * 360;
            if (strpos(strtolower($dataRowParams[0]), 'other') === 0 || strpos(strtolower($dataRowParams[0]), 'persons') === 0 || strtolower($dataRowParams[0]) ==
                'unknown') {
                $pdf->SetFillColorArray(array(
                    189,
                    195,
                    199), false);
            } else
                if (strtolower($dataRowParams[0]) == 'yes') {
                    $pdf->SetFillColorArray(array(
                        46,
                        204,
                        113), false);
                } else
                    if (strtolower($dataRowParams[0]) == 'no') {
                        $pdf->SetFillColorArray(array(
                            231,
                            76,
                            60), false);
                    } else
                        if (strtolower($dataRowParams[0]) == 'male' || strtolower($dataRowParams[0]) ==
                            'm') {
                            $pdf->SetFillColorArray(array(
                                52,
                                152,
                                219), false);
                        } else
                            if (strtolower($dataRowParams[0]) == 'female' || strtolower($dataRowParams[0]) ==
                                'f') {
                                $pdf->SetFillColorArray(array(
                                    255,
                                    105,
                                    180), false);
                            } else {
                                $pdf->SetFillColorArray($colours[$counter], false);
                            }
                            $pdf->PieSector($pageWidth / 2, $pageHeight / 2, 50, $startLocation - $widthDegrees,
                                $startLocation, 'F', false, 0, 0);
            $startLocation -= $widthDegrees;
        }
        $counter++;
    }

    $pdf->SetFillColor(255, 255, 255);
    $counter = 0;
    $startLocation = 250;
    $labelWidth = 50;
    $labelAtTop = false;
    foreach ($data as $dataLine) {
        if (strpos($dataLine, ':') !== false) {
            $dataRowParams = explode(":", $dataLine);
            $widthDegrees = (intval(trim($dataRowParams[1])) / $total) * 360;
            $targetTextDegrees = $startLocation - ($widthDegrees / 2);
            $targetTextDegreesRadians = deg2rad($targetTextDegrees);
            $xpos = cos($targetTextDegreesRadians) * 55;
            $ypos = sin($targetTextDegreesRadians) * -55;
            $subtitle = $dataRowParams[0];
            if (strlen($subtitle) > 50) {
                $subtitle = substr($subtitle, 0, 47) . '&hellip;';
            }
            $subtitle = $subtitle . '<br />' . number_format(intval(trim($dataRowParams[1])));
            $align = 'C';
            $verticalAlignChange = -$labelWidth / 2;
            $horizontalAlignChange = -3;
            if (cos($targetTextDegreesRadians) > 0.2) {
                $align = 'L';
                $verticalAlignChange = 0;
            } else
                if (cos($targetTextDegreesRadians) < -0.2) {
                    $align = 'R';
                    $verticalAlignChange = -$labelWidth;
                }
            if (sin($targetTextDegreesRadians) > 0.9) {
                $horizontalAlignChange -= 7;
                $labelAtTop = true;
                if (strlen($dataRowParams[0]) > 20) {
                    $horizontalAlignChange -= 6;
                }
            } else
                if (sin($targetTextDegreesRadians) < -0.9) {
                    $horizontalAlignChange += 1;
                }

            $pdf->WriteHtmlCell($labelWidth, 25, $xpos + ($pageWidth / 2) + $verticalAlignChange,
                $ypos + ($pageHeight / 2) + $horizontalAlignChange, $subtitle, 1, 0, true, true,
                $align, false);
            $startLocation -= $widthDegrees;

        }
        $counter++;
    }

    $pdf->SetTextColor(255, 255, 255);
    $pdf->SetFont('helvetica', 'B', 36);
    $pdf->WriteHtmlCell($pageWidth, 100, 0, 64 + ($labelAtTop ? -10 : 0), $title, 0,
        0, false, true, 'C');
}

/** 
 * Render a tag cloud.
 * @author Peter
 * @param $pdf the current instance of the PDF to append.
 * @param $title the title content.
 * @param $content values to render.
 * @returns void
 */
function renderTag($pdf, $title, $content)
{
    renderPageWithbackground($pdf);
    $pageWidth = $pdf->GetPageWidth();
    $pageHeight = $pdf->GetPageHeight();

    $data = explode("\n", $content);
    $pdf->SetTextColor(255, 255, 255);
    $counter = 0;
    $deg = rand(0, 360);
    $minTop = null;
    $scale = 3;
    foreach ($data as $dataLine) {
        if (strpos($dataLine, ':') !== false) {
            $dataRowParams = explode(":", $dataLine);
            $pdf->SetFont('helvetica', 'B', intval($dataRowParams[1]));
            $left = intval(trim($dataRowParams[2]));
            $top = intval(trim($dataRowParams[3]));
            if ($minTop == null || $minTop > $top) {
                $minTop = $top;
            }
            $colVal = getHSL($deg, 0.5, (intval($dataRowParams[1]) / 50.0) + .1);
            $pdf->SetTextColorArray($colVal);
            $pdf->Text(($left / $scale) - 10, ($top / $scale) + 50, $dataRowParams[0]);
        }
        $counter++;
    }

    $pdf->SetTextColor(255, 255, 255);
    $pdf->SetFont('helvetica', 'B', 36);

    $pdf->WriteHtmlCell($pageWidth, 100, 0, ($minTop / $scale) + 30, $title, 0, 0, false, true,
        'C');
}

/** 
 * Render a bar chart.
 * @author Peter
 * @param $pdf the current instance of the PDF to append.
 * @param $title the title content.
 * @param $content values to render.
 * @param @gapRatio a decimal value of the gap ratio to use between columns.
 * @returns void
 */
function renderBarChart($pdf, $title, $content, $gapRatio = 0.1)
{
    renderPageWithbackground($pdf);
    $pageWidth = $pdf->GetPageWidth();
    $pageHeight = $pdf->GetPageHeight();
    $deg = rand(0, 360);
    $data = explode("\n", $content);
    $pdf->SetTextColor(255, 255, 255);
    $counter = 0;
    $total = 0;
    $max = null;
    $min = null;
    $numberOfItems = 0;
    foreach ($data as $dataLine) {
        if (strpos($dataLine, ':') !== false) {
            $dataRowParams = explode(':', $dataLine);
            $value = intval(trim($dataRowParams[1]));
            if ($max == null || $max < $value) {
                $max = $value;
            }
            if ($min == null || $min > $value) {
                $min = $value;
            }
            $total += $value;
            $numberOfItems++;
        }
        $counter++;
    }

    $pdf->SetTextColor(255, 255, 255);
    $pdf->SetFont('helvetica', '', 11);

    $increment = ceil(($max - $min) / 5.0);
    $factor = floor(pow(10.0, floor(log10($increment))));
    if ($factor > 1) {
        $increment = floor($increment / $factor) * $factor;
    }

    if ($increment < 1) {
        $increment = 1;
    }

    if ($max == null) {
        $max = 0;
    }

    $width = 170;
    $height = 100;
    $barWidth = ($width * (1 - $gapRatio)) / $numberOfItems;
    $gapWidth = ($width * $gapRatio) / $numberOfItems;
    $counter = 0;
    $leftPosition = 30;
    $topPosition = 100;

    $pdf->SetDrawColor(200, 200, 200);
    if ($max / $increment < 20) { // [TODO] Clean up...
        $pdf->Line($leftPosition - 3, $topPosition + $height, $leftPosition - 3, $topPosition +
            $height - ($height * (($increment * ceil($max / $increment)) / $max)));
        for ($line = 0; $line < ($max / $increment) + 1; $line++) {
            // Draw left hand side scale labels.
            $tmpTopPositon = $topPosition + $height - ($height * (($increment * $line) / $max));
            $pdf->Line($leftPosition - 6, $tmpTopPositon, $leftPosition - 3, $tmpTopPositon);
            $pdf->WriteHtmlCell(20, 10, $leftPosition - 25, $tmpTopPositon - 3,
                number_format($increment * $line), 0, 0, false, true, 'R');
        }
    }

    foreach ($data as $dataLine) {
        // Draw columns.
        if (strpos($dataLine, ':') !== false) {
            $dataRowParams = explode(':', $dataLine);
            $value = intval(trim($dataRowParams[1]));
            $colVal = getHSL($deg, 0.5, ($value / $max / 2) + .1);
            $pdf->SetFillColorArray($colVal);
            $pdf->Rect($leftPosition, $topPosition + $height - ($height * ($value / $max)),
                $barWidth, $height * ($value / $max), 'F');

            // Draw horizontal label.
            if (count($data) > 20) {
                if ($counter % ceil(count($data) / 20) == 0) {
                    $pdf->Line($leftPosition + ($barWidth / 2), 100 + $height, $leftPosition + ($barWidth /
                        2), 100 + $height + 2.5);

                    $pdf->rotate(270, $leftPosition + ($barWidth / 2) - 2, $topPosition + $height +
                        4);
                    $pdf->WriteHtmlCell(100, 90, $leftPosition - 2, $topPosition + $height + 0, $dataRowParams[0],
                        0, 0, false, true, 'L');
                    $pdf->rotate(-270, $leftPosition + ($barWidth / 2) - 2, $topPosition + $height +
                        4);
                }
            } else {
                $pdf->WriteHtmlCell($barWidth + ($gapWidth * 2), 10, $leftPosition - $gapWidth,
                    $topPosition + $height + 2, $dataRowParams[0], 0, 0, false, true, 'C');
            }

            $leftPosition += $barWidth + $gapWidth;
        }
        $counter++;
    }


    $pdf->Line(27, $topPosition + $height, 30 + $width, $topPosition + $height);
    $pdf->SetDrawColor('');

    $pdf->SetTextColor(255, 255, 255);
    $pdf->SetFont('helvetica', 'B', 36);

    $pdf->WriteHtmlCell($pageWidth, 100, 0, 65, $title, 0, 0, false, true, 'C');
}

/** 
 * Render a fact.
 * @author Peter
 * @param $pdf the current instance of the PDF to append.
 * @param $title the title content.
 * @param $content values to render.
 * @returns void
 */
function renderFact($pdf, $title, $content)
{
    renderPageWithbackground($pdf);
    $pageWidth = $pdf->GetPageWidth();
    $pageHeight = $pdf->GetPageHeight();
    $data = explode("\n", $content);
    $top = 0;
    $pdf->SetTextColor(255, 255, 255);
    $fact = $data[0];
    $colours = getFlatColours();
    $colour = $colours[rand(0, 2)];

    $pdf->Circle(105, 145, 90, 0, 360, 'FD', array(), $colour, 2);
    $pdf->SetFont('helvetica', 'B', 72);
    $pdf->MultiCell(110, 45, $title, 0, 'C', false, 1, 50, 90, true, 0, false, true,
        0, false, true, 0, 'B', true);
    $pdf->SetFont('helvetica', 'B', 80);
    $pdf->WriteHtmlCell(160, 100, 25, 130, $fact, 0, 0, false, true, 'C');
}

/** 
 * Render a top 10 listing table.
 * @author Peter
 * @param $pdf the current instance of the PDF to append.
 * @param $title the title content.
 * @param $content values to render.
 * @returns void
 */
function renderTop10($pdf, $title, $content)
{
    renderPageWithbackground($pdf);
    $pageWidth = $pdf->GetPageWidth();
    $pageHeight = $pdf->GetPageHeight();
    $pdf->SetTextColor(255, 255, 254);
    $dataLines = explode("\n", $content);
    $counter = 0;
    $top = 50;
    $pdf->SetFont('helvetica', '', 14);
    $pdf->SetDrawColor(200, 200, 200);
    $pdf->Line(30, $top + 43, 180, $top + 43);
    foreach ($dataLines as $dataLine) {
        if (strpos($dataLine, ':') !== false) {
            $params = explode(':', trim($dataLine));
            $pdf->SetFont('helvetica', 'B', 14);
            $pdf->WriteHtmlCell(100, 30, 35, $top, '#' . ($counter + 1) . '.', 0, 0, false, false,
                'L');
            $pdf->SetFont('helvetica', '', 14);
            $subtitle = $params[0];
            if (isset($subtitle[25])) {
                $subtitle = substr($subtitle, 0, 24) . '...';
            }

            $pdf->WriteHtmlCell(100, 30, 47, $top, $subtitle, 0, 0, false, false, 'L');
            $pdf->WriteHtmlCell(100, 30, 76, $top, number_format(intval(trim($params[1]))),
                0, 0, false, false, 'R');

            $pdf->Line(30, $top + 58, 180, $top + 58);
            $top += 15;
        }
        $counter++;
    }

    $pdf->SetDrawColor('');
    $pdf->SetFont('helvetica', 'B', 36);
    $pdf->WriteHtmlCell($pageWidth - 6, 100, 3, 60, $title, 0, 0, false, true, 'C');
}

/**
 * Render page with background.
 * @author Peter
 * @pram $pdf the current instance of the PDF to append.
 * @returns void
 */
function renderPageWithbackground($pdf)
{
    $pdf->AddPage();
    $pdf->SetFillColor(20, 20, 20);
    $pdf->Rect(0, 0, $pdf->GetPageWidth(), $pdf->GetPageHeight(), 'F');
}

/**
 * Get flat colours.
 * Colours via http://flatuicolors.com/
 * @returns an array, of array RGB colours.
 */
function getFlatColours()
{
    return array(
        array(
            52,
            152,
            219),
        array(
            231,
            76,
            60),
        array(
            46,
            204,
            113),
        array(
            241,
            196,
            15),
        array(
            155,
            89,
            182),
        array(
            52,
            73,
            94),

        array(
            230,
            126,
            34));
}


/**
 * Get  RGB value array for provided hue, saturation and lightness values.
 * $hue = [0,360)
 * $saturation = [0,1]
 * $lightness = [0,1]
 * @returns an array representing red, green, blue values for the provided  hue, saturation and lightness values.
 */
function getHSL($hue, $saturation, $lightness, $fixed = false)
{
    $chroma = (1 - abs((2 * $lightness) - 1)) * $saturation;
    $h1 = $hue / 60;
    $x = $chroma * (1 - abs(fmod($h1, 2) - 1));
    if (!$fixed) {
        $x = $chroma * (1 - abs($h1 % 2 - 1));
    }
    $r1 = 0;
    $g1 = 0;
    $b1 = 0;
    if ($h1 < 1) {
        $r1 = $chroma;
        $g1 = $x;
        $b1 = 0;
    } else
        if ($h1 < 2) {
            $r1 = $x;
            $g1 = $chroma;
            $b1 = 0;
        } else
            if ($h1 < 3) {
                $r1 = 0;
                $g1 = $chroma;
                $b1 = $x;
            } else
                if ($h1 < 4) {
                    $r1 = 0;
                    $g1 = $x;
                    $b1 = $chroma;
                } else
                    if ($h1 < 5) {
                        $r1 = $x;
                        $g1 = 0;
                        $b1 = $chroma;
                    } else
                        if ($h1 < 6) {
                            $r1 = $chroma;
                            $g1 = 0;
                            $b1 = $x;
                        }

    $m = $lightness - (0.5 * $chroma);
    $r = ($r1 + $m) * 255;
    $g = ($g1 + $m) * 255;
    $b = ($b1 + $m) * 255;

    return array(
        $r,
        $g,
        $b);
}

?>
