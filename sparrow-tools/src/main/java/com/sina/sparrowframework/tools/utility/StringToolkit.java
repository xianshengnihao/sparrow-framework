package com.sina.sparrowframework.tools.utility;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public abstract class StringToolkit  {


    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * 替换指定字符串的指定区间内字符为"*"
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @return 替换后的字符串
     * @since 4.1.14
     */
    public static String hide(CharSequence str, int startInclude, int endExclude) {
        return replacece(str, startInclude, endExclude, '*');
    }

    /**
     * 替换指定字符串的指定区间内字符为固定字符
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @param replacedChar 被替换的字符
     * @return 替换后的字符串
     * @since 3.2.1
     */
    public static String replacece(CharSequence str, int startInclude, int endExclude, char replacedChar) {
        if (isEmpty(str)) {
            return str(str);
        }
        final int strLength = str.length();
        if (startInclude > strLength) {
            return str(str);
        }
        if (endExclude > strLength) {
            endExclude = strLength;
        }
        if (startInclude > endExclude) {
            // 如果起始位置大于结束位置，不替换
            return str(str);
        }

        final char[] chars = new char[strLength];
        for (int i = 0; i < strLength; i++) {
            if (i >= startInclude && i < endExclude) {
                chars[i] = replacedChar;
            } else {
                chars[i] = str.charAt(i);
            }
        }
        return new String(chars);
    }


    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }


    //---------------------------------------------------------------------
    // General convenience methods for working with Strings
    //---------------------------------------------------------------------

    /**
     * Check whether the given {@code String} is empty.
     * <p>This method accepts any Object as an argument, comparing it to
     * {@code null} and the empty String. As a consequence, this method
     * will never return {@code true} for a non-null non-String object.
     * <p>The Object signature is useful for general attribute handling code
     * that commonly deals with Strings but generally has to iterate over
     * Objects since attributes may e.g. be primitive value objects as well.
     * @param str the candidate String
     * @since 3.2.1
     */
    public static boolean isEmpty( Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * Check that the given {@code CharSequence} is neither {@code null} nor
     * of length 0.
     * <p>Note: this method returns {@code true} for a {@code CharSequence}
     * that purely consists of whitespace.
     * <p><pre class="code">
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
     * @see #hasText(String)
     */
    public static boolean hasLength( CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given {@code String} is neither {@code null} nor of length 0.
     * <p>Note: this method returns {@code true} for a {@code String} that
     * purely consists of whitespace.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null} and has length
     * @see #hasLength(CharSequence)
     * @see #hasText(String)
     */
    public static boolean hasLength( String str) {
        return (str != null && !str.isEmpty());
    }

    /**
     * Check whether the given {@code CharSequence} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code CharSequence} is not {@code null}, its length is greater than
     * 0, and it contains at least one non-whitespace character.
     * <p><pre class="code">
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null},
     * its length is greater than 0, and it does not contain whitespace only
     * @see Character#isWhitespace
     */
    public static boolean hasText( CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    public static boolean hasText( String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }



    /**
     * Check whether the given {@code String} contains any whitespace characters.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not empty and
     * contains at least 1 whitespace character
     * @see #containsWhitespace(CharSequence)
     */
    public static boolean containsWhitespace( String str) {
        return containsWhitespace((CharSequence) str);
    }

    /**
     * Trim leading and trailing whitespace from the given {@code String}.
     * @param str the {@code String} to check
     * @return the trimmed {@code String}
     * @see java.lang.Character#isWhitespace
     */
    public static String trimWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }

        int beginIndex = 0;
        int endIndex = str.length() - 1;

        while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
            beginIndex++;
        }

        while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
            endIndex--;
        }

        return str.substring(beginIndex, endIndex + 1);
    }

    /**
     * Trim <i>all</i> whitespace from the given {@code String}:
     * leading, trailing, and in between characters.
     * @param str the {@code String} to check
     * @return the trimmed {@code String}
     * @see java.lang.Character#isWhitespace
     */
    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }

        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Trim leading whitespace from the given {@code String}.
     * @param str the {@code String} to check
     * @return the trimmed {@code String}
     * @see java.lang.Character#isWhitespace
     */
    public static String trimLeadingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Trim trailing whitespace from the given {@code String}.
     * @param str the {@code String} to check
     * @return the trimmed {@code String}
     * @see java.lang.Character#isWhitespace
     */
    public static String trimTrailingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Trim all occurrences of the supplied leading character from the given {@code String}.
     * @param str the {@code String} to check
     * @param leadingCharacter the leading character to be trimmed
     * @return the trimmed {@code String}
     */
    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Trim all occurrences of the supplied trailing character from the given {@code String}.
     * @param str the {@code String} to check
     * @param trailingCharacter the trailing character to be trimmed
     * @return the trimmed {@code String}
     */
    public static String trimTrailingCharacter(String str, char trailingCharacter) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Test if the given {@code String} starts with the specified prefix,
     * ignoring upper/lower case.
     * @param str the {@code String} to check
     * @param prefix the prefix to look for
     * @see java.lang.String#startsWith
     */
    public static boolean startsWithIgnoreCase( String str,  String prefix) {
        return (str != null && prefix != null && str.length() >= prefix.length() &&
                str.regionMatches(true, 0, prefix, 0, prefix.length()));
    }

    /**
     * Test if the given {@code String} ends with the specified suffix,
     * ignoring upper/lower case.
     * @param str the {@code String} to check
     * @param suffix the suffix to look for
     * @see java.lang.String#endsWith
     */
    public static boolean endsWithIgnoreCase( String str,  String suffix) {
        return (str != null && suffix != null && str.length() >= suffix.length() &&
                str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
    }

    /**
     * Test whether the given string matches the given substring
     * at the given index.
     * @param str the original string (or StringBuilder)
     * @param index the index in the original string to start matching against
     * @param substring the substring to match at the given index
     */
    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Count the occurrences of the substring {@code sub} in string {@code str}.
     * @param str string to search in
     * @param sub string to search for
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (!hasLength(str) || !hasLength(sub)) {
            return 0;
        }

        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }



    /**
     * Delete all occurrences of the given substring.
     * @param inString the original {@code String}
     * @param pattern the pattern to delete all occurrences of
     * @return the resulting {@code String}
     */
    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, "");
    }

    /**
     * Delete any character in a given {@code String}.
     * @param inString the original {@code String}
     * @param charsToDelete a set of characters to delete.
     * E.g. "az\n" will delete 'a's, 'z's and new lines.
     * @return the resulting {@code String}
     */
    public static String deleteAny(String inString,  String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }

        StringBuilder sb = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    //---------------------------------------------------------------------
    // Convenience methods for working with formatted Strings
    //---------------------------------------------------------------------

    /**
     * Quote the given {@code String} with single quotes.
     * @param str the input {@code String} (e.g. "myString")
     * @return the quoted {@code String} (e.g. "'myString'"),
     * or {@code null} if the input was {@code null}
     */

    public static String quote( String str) {
        return (str != null ? "'" + str + "'" : null);
    }

    /**
     * Turn the given Object into a {@code String} with single quotes
     * if it is a {@code String}; keeping the Object as-is else.
     * @param obj the input Object (e.g. "myString")
     * @return the quoted {@code String} (e.g. "'myString'"),
     * or the input object as-is if not a {@code String}
     */

    public static Object quoteIfString( Object obj) {
        return (obj instanceof String ? quote((String) obj) : obj);
    }

    /**
     * Unqualify a string qualified by a '.' dot character. For example,
     * "this.name.is.qualified", returns "qualified".
     * @param qualifiedName the qualified name
     */
    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    /**
     * Unqualify a string qualified by a separator character. For example,
     * "this:name:is:qualified" returns "qualified" if using a ':' separator.
     * @param qualifiedName the qualified name
     * @param separator the separator
     */
    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }


    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (!hasLength(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        }
        else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    /**
     * Extract the filename from the given Java resource path,
     * e.g. {@code "mypath/myfile.txt" -> "myfile.txt"}.
     * @param path the file path (may be {@code null})
     * @return the extracted filename, or {@code null} if none
     */

    public static String getFilename( String path) {
        if (path == null) {
            return null;
        }

        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }

    /**
     * Extract the filename extension from the given Java resource path,
     * e.g. "mypath/myfile.txt" -> "txt".
     * @param path the file path (may be {@code null})
     * @return the extracted filename extension, or {@code null} if none
     */

    public static String getFilenameExtension( String path) {
        if (path == null) {
            return null;
        }

        int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (extIndex == -1) {
            return null;
        }

        int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (folderIndex > extIndex) {
            return null;
        }

        return path.substring(extIndex + 1);
    }

    /**
     * Strip the filename extension from the given Java resource path,
     * e.g. "mypath/myfile.txt" -> "mypath/myfile".
     * @param path the file path
     * @return the path with stripped filename extension
     */
    public static String stripFilenameExtension(String path) {
        int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (extIndex == -1) {
            return path;
        }

        int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (folderIndex > extIndex) {
            return path;
        }

        return path.substring(0, extIndex);
    }

    /**
     * Apply the given relative path to the given Java resource path,
     * assuming standard Java folder separation (i.e. "/" separators).
     * @param path the path to start from (usually a full file path)
     * @param relativePath the relative path to apply
     * (relative to the full file path above)
     * @return the full file path that results from applying the relative path
     */
    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        }
        else {
            return relativePath;
        }
    }

    /**
     * Normalize the path by suppressing sequences like "path/.." and
     * inner simple dots.
     * <p>The result is convenient for path comparison. For other uses,
     * notice that Windows separators ("\") are replaced by simple slashes.
     * @param path the original path
     * @return the normalized path
     */
    public static String cleanPath(String path) {
        if (!hasLength(path)) {
            return path;
        }
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(':');
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains("/")) {
                prefix = "";
            }
            else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        List<String> pathElements = new LinkedList<>();
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            }
            else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            }
            else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                }
                else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }

        return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    /**
     * Compare two paths after normalization of them.
     * @param path1 first path for comparison
     * @param path2 second path for comparison
     * @return whether the two paths are equivalent after normalization
     */
    public static boolean pathEquals(String path1, String path2) {
        return cleanPath(path1).equals(cleanPath(path2));
    }

    /**
     * Decode the given encoded URI component value. Based on the following rules:
     * <ul>
     * <li>Alphanumeric characters {@code "a"} through {@code "z"}, {@code "A"} through {@code "Z"},
     * and {@code "0"} through {@code "9"} stay the same.</li>
     * <li>Special characters {@code "-"}, {@code "_"}, {@code "."}, and {@code "*"} stay the same.</li>
     * <li>A sequence "{@code %<i>xy</i>}" is interpreted as a hexadecimal representation of the character.</li>
     * </ul>
     * @param source the encoded String
     * @param charset the character set
     * @return the decoded value
     * @throws IllegalArgumentException when the given source contains invalid encoded sequences
     * @since 5.0
     * @see java.net.URLDecoder#decode(String, String)
     */
    public static String uriDecode(String source, Charset charset) {
        int length = source.length();
        if (length == 0) {
            return source;
        }
        Assert.notNull(charset, "Charset must not be null");

        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        boolean changed = false;
        for (int i = 0; i < length; i++) {
            int ch = source.charAt(i);
            if (ch == '%') {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                    }
                    bos.write((char) ((u << 4) + l));
                    i += 2;
                    changed = true;
                }
                else {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
            }
            else {
                bos.write(ch);
            }
        }
        return (changed ? new String(bos.toByteArray(), charset) : source);
    }

    /**
     * Parse the given {@code String} value into a {@link Locale}, accepting
     * the {@link Locale#toString} format as well as BCP 47 language tags.
     * @param localeValue the locale value: following either {@code Locale's}
     * {@code toString()} format ("en", "en_UK", etc), also accepting spaces as
     * separators (as an alternative to underscores), or BCP 47 (e.g. "en-UK")
     * as specified by {@link Locale#forLanguageTag} on Java 7+
     * @return a corresponding {@code Locale} instance, or {@code null} if none
     * @throws IllegalArgumentException in case of an invalid locale specification
     * @since 5.0.4
     * @see #parseLocaleString
     * @see Locale#forLanguageTag
     */

    public static Locale parseLocale(String localeValue) {
        String[] tokens = tokenizeLocaleSource(localeValue);
        if (tokens.length == 1) {
            return Locale.forLanguageTag(localeValue);
        }
        return parseLocaleTokens(localeValue, tokens);
    }

    /**
     * Parse the given {@code String} representation into a {@link Locale}.
     * <p>This is the inverse operation of {@link Locale#toString Locale's toString}.
     * @param localeString the locale {@code String}: following {@code Locale's}
     * {@code toString()} format ("en", "en_UK", etc), also accepting spaces as
     * separators (as an alternative to underscores)
     * <p>Note: This variant does not accept the BCP 47 language tag format.
     * Please use {@link #parseLocale} for lenient parsing of both formats.
     * @return a corresponding {@code Locale} instance, or {@code null} if none
     * @throws IllegalArgumentException in case of an invalid locale specification
     */

    public static Locale parseLocaleString(String localeString) {
        return parseLocaleTokens(localeString, tokenizeLocaleSource(localeString));
    }

    private static String[] tokenizeLocaleSource(String localeSource) {
        return tokenizeToStringArray(localeSource, "_ ", false, false);
    }


    private static Locale parseLocaleTokens(String localeString, String[] tokens) {
        String language = (tokens.length > 0 ? tokens[0] : "");
        String country = (tokens.length > 1 ? tokens[1] : "");
        validateLocalePart(language);
        validateLocalePart(country);

        String variant = "";
        if (tokens.length > 2) {
            // There is definitely a variant, and it is everything after the country
            // code sans the separator between the country code and the variant.
            int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
            // Strip off any leading '_' and whitespace, what's left is the variant.
            variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
            if (variant.startsWith("_")) {
                variant = trimLeadingCharacter(variant, '_');
            }
        }
        return (language.length() > 0 ? new Locale(language, country, variant) : null);
    }

    private static void validateLocalePart(String localePart) {
        for (int i = 0; i < localePart.length(); i++) {
            char ch = localePart.charAt(i);
            if (ch != ' ' && ch != '_' && ch != '#' && !Character.isLetterOrDigit(ch)) {
                throw new IllegalArgumentException(
                        "Locale part \"" + localePart + "\" contains invalid characters");
            }
        }
    }

    /**
     * Determine the RFC 3066 compliant language tag,
     * as used for the HTTP "Accept-Language" header.
     * @param locale the Locale to transform to a language tag
     * @return the RFC 3066 compliant language tag as {@code String}
     * @deprecated as of 5.0.4, in favor of {@link Locale#toLanguageTag()}
     */
    @Deprecated
    public static String toLanguageTag(Locale locale) {
        return locale.getLanguage() + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
    }

    /**
     * Parse the given {@code timeZoneString} value into a {@link TimeZone}.
     * @param timeZoneString the time zone {@code String}, following {@link TimeZone#getTimeZone(String)}
     * but throwing {@link IllegalArgumentException} in case of an invalid time zone specification
     * @return a corresponding {@link TimeZone} instance
     * @throws IllegalArgumentException in case of an invalid time zone specification
     */
    public static TimeZone parseTimeZoneString(String timeZoneString) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
        if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT")) {
            // We don't want that GMT fallback...
            throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
        }
        return timeZone;
    }


    //---------------------------------------------------------------------
    // Convenience methods for working with String arrays
    //---------------------------------------------------------------------

    /**
     * Append the given {@code String} to the given {@code String} array,
     * returning a new array consisting of the input array contents plus
     * the given {@code String}.
     * @param array the array to append to (can be {@code null})
     * @param str the {@code String} to append
     * @return the new array (never {@code null})
     */
    public static String[] addStringToArray( String[] array, String str) {
        if (ObjectToolkit.isEmpty(array)) {
            return new String[] {str};
        }

        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    /**
     * Concatenate the given {@code String} arrays into one,
     * with overlapping array elements included twice.
     * <p>The order of elements in the original arrays is preserved.
     * @param array1 the first array (can be {@code null})
     * @param array2 the second array (can be {@code null})
     * @return the new array ({@code null} if both given arrays were {@code null})
     */

    public static String[] concatenateStringArrays( String[] array1,  String[] array2) {
        if (ObjectToolkit.isEmpty(array1)) {
            return array2;
        }
        if (ObjectToolkit.isEmpty(array2)) {
            return array1;
        }

        String[] newArr = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, newArr, 0, array1.length);
        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
        return newArr;
    }

    /**
     * Merge the given {@code String} arrays into one, with overlapping
     * array elements only included once.
     * <p>The order of elements in the original arrays is preserved
     * (with the exception of overlapping elements, which are only
     * included on their first occurrence).
     * @param array1 the first array (can be {@code null})
     * @param array2 the second array (can be {@code null})
     * @return the new array ({@code null} if both given arrays were {@code null})
     * @deprecated as of 4.3.15, in favor of manual merging via {@link LinkedHashSet}
     * (with every entry included at most once, even entries within the first array)
     */
    @Deprecated

    public static String[] mergeStringArrays( String[] array1,  String[] array2) {
        if (ObjectToolkit.isEmpty(array1)) {
            return array2;
        }
        if (ObjectToolkit.isEmpty(array2)) {
            return array1;
        }

        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(array1));
        for (String str : array2) {
            if (!result.contains(str)) {
                result.add(str);
            }
        }
        return toStringArray(result);
    }

    /**
     * Turn given source {@code String} array into sorted array.
     * @param array the source array
     * @return the sorted array (never {@code null})
     */
    public static String[] sortStringArray(String[] array) {
        if (ObjectToolkit.isEmpty(array)) {
            return new String[0];
        }

        Arrays.sort(array);
        return array;
    }

    /**
     * Copy the given {@code Collection} into a {@code String} array.
     * <p>The {@code Collection} must contain {@code String} elements only.
     * @param collection the {@code Collection} to copy
     * @return the {@code String} array
     */
    public static String[] toStringArray(Collection<String> collection) {
        return collection.toArray(new String[0]);
    }

    /**
     * Copy the given Enumeration into a {@code String} array.
     * The Enumeration must contain {@code String} elements only.
     * @param enumeration the Enumeration to copy
     * @return the {@code String} array
     */
    public static String[] toStringArray(Enumeration<String> enumeration) {
        return toStringArray(Collections.list(enumeration));
    }

    /**
     * Trim the elements of the given {@code String} array,
     * calling {@code String.trim()} on each of them.
     * @param array the original {@code String} array
     * @return the resulting array (of the same size) with trimmed elements
     */
    public static String[] trimArrayElements( String[] array) {
        if (ObjectToolkit.isEmpty(array)) {
            return new String[0];
        }

        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String element = array[i];
            result[i] = (element != null ? element.trim() : null);
        }
        return result;
    }

    /**
     * Remove duplicate strings from the given array.
     * <p>As of 4.2, it preserves the original order, as it uses a {@link LinkedHashSet}.
     * @param array the {@code String} array
     * @return an array without duplicates, in natural sort order
     */
    public static String[] removeDuplicateStrings(String[] array) {
        if (ObjectToolkit.isEmpty(array)) {
            return array;
        }

        Set<String> set = new LinkedHashSet<>();
        for (String element : array) {
            set.add(element);
        }
        return toStringArray(set);
    }



    /**
     * Take an array of strings and split each element based on the given delimiter.
     * A {@code Properties} instance is then generated, with the left of the delimiter
     * providing the key, and the right of the delimiter providing the value.
     * <p>Will trim both the key and value before adding them to the {@code Properties}.
     * @param array the array to process
     * @param delimiter to split each element using (typically the equals symbol)
     * @return a {@code Properties} instance representing the array contents,
     * or {@code null} if the array to process was {@code null} or empty
     */

    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    /**
     * Take an array of strings and split each element based on the given delimiter.
     * A {@code Properties} instance is then generated, with the left of the
     * delimiter providing the key, and the right of the delimiter providing the value.
     * <p>Will trim both the key and value before adding them to the
     * {@code Properties} instance.
     * @param array the array to process
     * @param delimiter to split each element using (typically the equals symbol)
     * @param charsToDelete one or more characters to remove from each element
     * prior to attempting the split operation (typically the quotation mark
     * symbol), or {@code null} if no removal should occur
     * @return a {@code Properties} instance representing the array contents,
     * or {@code null} if the array to process was {@code null} or empty
     */

    public static Properties splitArrayElementsIntoProperties(
            String[] array, String delimiter,  String charsToDelete) {

        if (ObjectToolkit.isEmpty(array)) {
            return null;
        }

        Properties result = new Properties();
        for (String element : array) {
            if (charsToDelete != null) {
                element = deleteAny(element, charsToDelete);
            }
            String[] splittedElement = split(element, delimiter);
            if (splittedElement == null) {
                continue;
            }
            result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
        }
        return result;
    }

    /**
     * Tokenize the given {@code String} into a {@code String} array via a
     * {@link StringTokenizer}.
     * <p>Trims tokens and omits empty tokens.
     * <p>The given {@code delimiters} string can consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using {@link #delimitedListToStringArray}.
     * @param str the {@code String} to tokenize
     * @param delimiters the delimiter characters, assembled as a {@code String}
     * (each of the characters is individually considered as a delimiter)
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see String#trim()
     * @see #delimitedListToStringArray
     */
    public static String[] tokenizeToStringArray( String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     * Tokenize the given {@code String} into a {@code String} array via a
     * {@link StringTokenizer}.
     * <p>The given {@code delimiters} string can consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using {@link #delimitedListToStringArray}.
     * @param str the {@code String} to tokenize
     * @param delimiters the delimiter characters, assembled as a {@code String}
     * (each of the characters is individually considered as a delimiter)
     * @param trimTokens trim the tokens via {@link String#trim()}
     * @param ignoreEmptyTokens omit empty tokens from the result array
     * (only applies to tokens that are empty after trimming; StringTokenizer
     * will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see String#trim()
     * @see #delimitedListToStringArray
     */
    public static String[] tokenizeToStringArray(
             String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return new String[0];
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    /**
     * Take a {@code String} that is a delimited list and convert it into a
     * {@code String} array.
     * <p>A single {@code delimiter} may consist of more than one character,
     * but it will still be considered as a single delimiter string, rather
     * than as bunch of potential delimiter characters, in contrast to
     * {@link #tokenizeToStringArray}.
     * @param str the input {@code String}
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @return an array of the tokens in the list
     * @see #tokenizeToStringArray
     */
    public static String[] delimitedListToStringArray( String str,  String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    /**
     * Take a {@code String} that is a delimited list and convert it into
     * a {@code String} array.
     * <p>A single {@code delimiter} may consist of more than one character,
     * but it will still be considered as a single delimiter string, rather
     * than as bunch of potential delimiter characters, in contrast to
     * {@link #tokenizeToStringArray}.
     * @param str the input {@code String}
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @param charsToDelete a set of characters to delete; useful for deleting unwanted
     * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a {@code String}
     * @return an array of the tokens in the list
     * @see #tokenizeToStringArray
     */
    public static String[] delimitedListToStringArray(
             String str,  String delimiter,  String charsToDelete) {

        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[] {str};
        }

        List<String> result = new ArrayList<>();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        }
        else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    /**
     * Convert a comma delimited list (e.g., a row from a CSV file) into an
     * array of strings.
     * @param str the input {@code String}
     * @return an array of strings, or the empty array in case of empty input
     */
    public static String[] commaDelimitedListToStringArray( String str) {
        return delimitedListToStringArray(str, ",");
    }

    /**
     * Convert a comma delimited list (e.g., a row from a CSV file) into a set.
     * <p>Note that this will suppress duplicates, and as of 4.2, the elements in
     * the returned set will preserve the original order in a {@link LinkedHashSet}.
     * @param str the input {@code String}
     * @return a set of {@code String} entries in the list
     * @see #removeDuplicateStrings(String[])
     */
    public static Set<String> commaDelimitedListToSet( String str) {
        Set<String> set = new LinkedHashSet<>();
        String[] tokens = commaDelimitedListToStringArray(str);
        for (String token : tokens) {
            set.add(token);
        }
        return set;
    }

    /**
     * Convert a {@link Collection} to a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     * @param coll the {@code Collection} to convert
     * @param delim the delimiter to use (typically a ",")
     * @param prefix the {@code String} to start each element with
     * @param suffix the {@code String} to end each element with
     * @return the delimited {@code String}
     */
    public static String collectionToDelimitedString(
             Collection<?> coll, String delim, String prefix, String suffix) {

        if (CollectionToolkit.isEmpty(coll)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    /**
     * Convert a {@code Collection} into a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     * @param coll the {@code Collection} to convert
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited {@code String}
     */
    public static String collectionToDelimitedString( Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    /**
     * Convert a {@code Collection} into a delimited {@code String} (e.g., CSV).
     * <p>Useful for {@code toString()} implementations.
     * @param coll the {@code Collection} to convert
     * @return the delimited {@code String}
     */
    public static String collectionToCommaDelimitedString(Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    /**
     * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     * @param arr the array to display
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited {@code String}
     */
    public static String arrayToDelimitedString( Object[] arr, String delim) {
        if (ObjectToolkit.isEmpty(arr)) {
            return "";
        }
        if (arr.length == 1) {
            return ObjectToolkit.nullSafeToString(arr[0]);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * Convert a {@code String} array into a comma delimited {@code String}
     * (i.e., CSV).
     * <p>Useful for {@code toString()} implementations.
     * @param arr the array to display
     * @return the delimited {@code String}
     */
    public static String arrayToCommaDelimitedString( Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }


    /**
     * A String for a space character.
     *
     * @since 3.2
     */
    public static final String SPACE = " ";

    /**
     * The empty String {@code ""}.
     * @since 2.0
     */
    public static final String EMPTY = "";

    /**
     * A String for linefeed LF ("\n").
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     *      for Character and String Literals</a>
     * @since 3.2
     */
    public static final String LF = "\n";

    /**
     * A String for carriage return CR ("\r").
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     *      for Character and String Literals</a>
     * @since 3.2
     */
    public static final String CR = "\r";

    /**
     * Represents a failed index search.
     * @since 2.1
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * <p>{@code StringToolkit} instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * {@code StringToolkit.trim(" foo ");}.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringToolkit() {
        super();
    }

    // Empty checks
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringToolkit.isEmpty(null)      = true
     * StringToolkit.isEmpty("")        = true
     * StringToolkit.isEmpty(" ")       = false
     * StringToolkit.isEmpty("bob")     = false
     * StringToolkit.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Checks if a CharSequence is not empty ("") and not null.</p>
     *
     * <pre>
     * StringToolkit.isNotEmpty(null)      = false
     * StringToolkit.isNotEmpty("")        = false
     * StringToolkit.isNotEmpty(" ")       = true
     * StringToolkit.isNotEmpty("bob")     = true
     * StringToolkit.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     * @since 3.0 Changed signature from isNotEmpty(String) to isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * <p>Checks if any one of the CharSequences are empty ("") or null.</p>
     *
     * <pre>
     * StringToolkit.isAnyEmpty(null)             = true
     * StringToolkit.isAnyEmpty(null, "foo")      = true
     * StringToolkit.isAnyEmpty("", "bar")        = true
     * StringToolkit.isAnyEmpty("bob", "")        = true
     * StringToolkit.isAnyEmpty("  bob  ", null)  = true
     * StringToolkit.isAnyEmpty(" ", "bar")       = false
     * StringToolkit.isAnyEmpty("foo", "bar")     = false
     * </pre>
     *
     * @param css  the CharSequences to check, may be null or empty
     * @return {@code true} if any of the CharSequences are empty or null
     * @since 3.2
     */
    public static boolean isAnyEmpty(final CharSequence... css) {
        if (ObjectToolkit.isEmpty(css)) {
            return true;
        }
        for (final CharSequence cs : css){
            if (isEmpty(cs)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Checks if none of the CharSequences are empty ("") or null.</p>
     *
     * <pre>
     * StringToolkit.isNoneEmpty(null)             = false
     * StringToolkit.isNoneEmpty(null, "foo")      = false
     * StringToolkit.isNoneEmpty("", "bar")        = false
     * StringToolkit.isNoneEmpty("bob", "")        = false
     * StringToolkit.isNoneEmpty("  bob  ", null)  = false
     * StringToolkit.isNoneEmpty(" ", "bar")       = true
     * StringToolkit.isNoneEmpty("foo", "bar")     = true
     * </pre>
     *
     * @param css  the CharSequences to check, may be null or empty
     * @return {@code true} if none of the CharSequences are empty or null
     * @since 3.2
     */
    public static boolean isNoneEmpty(final CharSequence... css) {
        return !isAnyEmpty(css);
    }
    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringToolkit.isBlank(null)      = true
     * StringToolkit.isBlank("")        = true
     * StringToolkit.isBlank(" ")       = true
     * StringToolkit.isBlank("bob")     = false
     * StringToolkit.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringToolkit.isNotBlank(null)      = false
     * StringToolkit.isNotBlank("")        = false
     * StringToolkit.isNotBlank(" ")       = false
     * StringToolkit.isNotBlank("bob")     = true
     * StringToolkit.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     *  not empty and not null and not whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * <p>Checks if any one of the CharSequences are blank ("") or null and not whitespace only..</p>
     *
     * <pre>
     * StringToolkit.isAnyBlank(null)             = true
     * StringToolkit.isAnyBlank(null, "foo")      = true
     * StringToolkit.isAnyBlank(null, null)       = true
     * StringToolkit.isAnyBlank("", "bar")        = true
     * StringToolkit.isAnyBlank("bob", "")        = true
     * StringToolkit.isAnyBlank("  bob  ", null)  = true
     * StringToolkit.isAnyBlank(" ", "bar")       = true
     * StringToolkit.isAnyBlank("foo", "bar")     = false
     * </pre>
     *
     * @param css  the CharSequences to check, may be null or empty
     * @return {@code true} if any of the CharSequences are blank or null or whitespace only
     * @since 3.2
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ObjectToolkit.isEmpty(css)) {
            return true;
        }
        for (final CharSequence cs : css){
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Checks if none of the CharSequences are blank ("") or null and whitespace only..</p>
     *
     * <pre>
     * StringToolkit.isNoneBlank(null)             = false
     * StringToolkit.isNoneBlank(null, "foo")      = false
     * StringToolkit.isNoneBlank(null, null)       = false
     * StringToolkit.isNoneBlank("", "bar")        = false
     * StringToolkit.isNoneBlank("bob", "")        = false
     * StringToolkit.isNoneBlank("  bob  ", null)  = false
     * StringToolkit.isNoneBlank(" ", "bar")       = false
     * StringToolkit.isNoneBlank("foo", "bar")     = true
     * </pre>
     *
     * @param css  the CharSequences to check, may be null or empty
     * @return {@code true} if none of the CharSequences are blank or null or whitespace only
     * @since 3.2
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        return !isAnyBlank(css);
    }

    // Trim
    //-----------------------------------------------------------------------
    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String, handling {@code null} by returning
     * {@code null}.</p>
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #strip(String)}.</p>
     *
     * <p>To trim your choice of characters, use the
     * {@link #strip(String, String)} methods.</p>
     *
     * <pre>
     * StringToolkit.trim(null)          = null
     * StringToolkit.trim("")            = ""
     * StringToolkit.trim("     ")       = ""
     * StringToolkit.trim("abc")         = "abc"
     * StringToolkit.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, {@code null} if null String input
     */
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning {@code null} if the String is
     * empty ("") after the trim or if it is {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToNull(String)}.</p>
     *
     * <pre>
     * StringToolkit.trimToNull(null)          = null
     * StringToolkit.trimToNull("")            = null
     * StringToolkit.trimToNull("     ")       = null
     * StringToolkit.trimToNull("abc")         = "abc"
     * StringToolkit.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String,
     *  {@code null} if only chars &lt;= 32, empty or null String input
     * @since 2.0
     */
    public static String trimToNull(final String str) {
        final String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning an empty String ("") if the String
     * is empty ("") after the trim or if it is {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToEmpty(String)}.</p>
     *
     * <pre>
     * StringToolkit.trimToEmpty(null)          = ""
     * StringToolkit.trimToEmpty("")            = ""
     * StringToolkit.trimToEmpty("     ")       = ""
     * StringToolkit.trimToEmpty("abc")         = "abc"
     * StringToolkit.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     * @since 2.0
     */
    public static String trimToEmpty(final String str) {
        return str == null ? EMPTY : str.trim();
    }

    // Stripping
    //-----------------------------------------------------------------------
    /**
     * <p>Strips whitespace from the start and end of a String.</p>
     *
     * <p>This is similar to {@link #trim(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.strip(null)     = null
     * StringToolkit.strip("")       = ""
     * StringToolkit.strip("   ")    = ""
     * StringToolkit.strip("abc")    = "abc"
     * StringToolkit.strip("  abc")  = "abc"
     * StringToolkit.strip("abc  ")  = "abc"
     * StringToolkit.strip(" abc ")  = "abc"
     * StringToolkit.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to remove whitespace from, may be null
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(final String str) {
        return strip(str, null);
    }

    /**
     * <p>Strips whitespace from the start and end of a String  returning
     * {@code null} if the String is empty ("") after the strip.</p>
     *
     * <p>This is similar to {@link #trimToNull(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringToolkit.stripToNull(null)     = null
     * StringToolkit.stripToNull("")       = null
     * StringToolkit.stripToNull("   ")    = null
     * StringToolkit.stripToNull("abc")    = "abc"
     * StringToolkit.stripToNull("  abc")  = "abc"
     * StringToolkit.stripToNull("abc  ")  = "abc"
     * StringToolkit.stripToNull(" abc ")  = "abc"
     * StringToolkit.stripToNull(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to be stripped, may be null
     * @return the stripped String,
     *  {@code null} if whitespace, empty or null String input
     * @since 2.0
     */
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return str.isEmpty() ? null : str;
    }

    /**
     * <p>Strips whitespace from the start and end of a String  returning
     * an empty String if {@code null} input.</p>
     *
     * <p>This is similar to {@link #trimToEmpty(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringToolkit.stripToEmpty(null)     = ""
     * StringToolkit.stripToEmpty("")       = ""
     * StringToolkit.stripToEmpty("   ")    = ""
     * StringToolkit.stripToEmpty("abc")    = "abc"
     * StringToolkit.stripToEmpty("  abc")  = "abc"
     * StringToolkit.stripToEmpty("abc  ")  = "abc"
     * StringToolkit.stripToEmpty(" abc ")  = "abc"
     * StringToolkit.stripToEmpty(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to be stripped, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     * @since 2.0
     */
    public static String stripToEmpty(final String str) {
        return str == null ? EMPTY : strip(str, null);
    }

    /**
     * <p>Strips any of a set of characters from the start and end of a String.
     * This is similar to {@link String#trim()} but allows the characters
     * to be stripped to be controlled.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.
     * Alternatively use {@link #strip(String)}.</p>
     *
     * <pre>
     * StringToolkit.strip(null, *)          = null
     * StringToolkit.strip("", *)            = ""
     * StringToolkit.strip("abc", null)      = "abc"
     * StringToolkit.strip("  abc", null)    = "abc"
     * StringToolkit.strip("abc  ", null)    = "abc"
     * StringToolkit.strip(" abc ", null)    = "abc"
     * StringToolkit.strip("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(String str, final String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * <p>Strips any of a set of characters from the start of a String.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringToolkit.stripStart(null, *)          = null
     * StringToolkit.stripStart("", *)            = ""
     * StringToolkit.stripStart("abc", "")        = "abc"
     * StringToolkit.stripStart("abc", null)      = "abc"
     * StringToolkit.stripStart("  abc", null)    = "abc"
     * StringToolkit.stripStart("abc  ", null)    = "abc  "
     * StringToolkit.stripStart(" abc ", null)    = "abc "
     * StringToolkit.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripStart(final String str, final String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.isEmpty()) {
            return str;
        } else {
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * <p>Strips any of a set of characters from the end of a String.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringToolkit.stripEnd(null, *)          = null
     * StringToolkit.stripEnd("", *)            = ""
     * StringToolkit.stripEnd("abc", "")        = "abc"
     * StringToolkit.stripEnd("abc", null)      = "abc"
     * StringToolkit.stripEnd("  abc", null)    = "  abc"
     * StringToolkit.stripEnd("abc  ", null)    = "abc"
     * StringToolkit.stripEnd(" abc ", null)    = " abc"
     * StringToolkit.stripEnd("  abcyx", "xyz") = "  abc"
     * StringToolkit.stripEnd("120.00", ".0")   = "12"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the set of characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripEnd(final String str, final String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.isEmpty()) {
            return str;
        } else {
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    // StripAll
    //-----------------------------------------------------------------------
    /**
     * <p>Strips whitespace from the start and end of every String in an array.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>A new array is returned each time, except for length zero.
     * A {@code null} array will return {@code null}.
     * An empty array will return itself.
     * A {@code null} array entry will be ignored.</p>
     *
     * <pre>
     * StringToolkit.stripAll(null)             = null
     * StringToolkit.stripAll([])               = []
     * StringToolkit.stripAll(["abc", "  abc"]) = ["abc", "abc"]
     * StringToolkit.stripAll(["abc  ", null])  = ["abc", null]
     * </pre>
     *
     * @param strs  the array to remove whitespace from, may be null
     * @return the stripped Strings, {@code null} if null array input
     */
    public static String[] stripAll(final String... strs) {
        return stripAll(strs, null);
    }

    /**
     * <p>Strips any of a set of characters from the start and end of every
     * String in an array.</p>
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>A new array is returned each time, except for length zero.
     * A {@code null} array will return {@code null}.
     * An empty array will return itself.
     * A {@code null} array entry will be ignored.
     * A {@code null} stripChars will strip whitespace as defined by
     * {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringToolkit.stripAll(null, *)                = null
     * StringToolkit.stripAll([], *)                  = []
     * StringToolkit.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
     * StringToolkit.stripAll(["abc  ", null], null)  = ["abc", null]
     * StringToolkit.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
     * StringToolkit.stripAll(["yabcz", null], "yz")  = ["abc", null]
     * </pre>
     *
     * @param strs  the array to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped Strings, {@code null} if null array input
     */
    public static String[] stripAll(final String[] strs, final String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        final String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    /**
     * <p>Removes diacritics (~= accents) from a string. The case will not be altered.</p>
     * <p>For instance, '&agrave;' will be replaced by 'a'.</p>
     * <p>Note that ligatures will be left as is.</p>
     *
     * <pre>
     * StringToolkit.stripAccents(null)                = null
     * StringToolkit.stripAccents("")                  = ""
     * StringToolkit.stripAccents("control")           = "control"
     * StringToolkit.stripAccents("&eacute;clair")     = "eclair"
     * </pre>
     *
     * @param input String to be stripped
     * @return input text with diacritics removed
     *
     * @since 3.0
     */
    // See also Lucene's ASCIIFoldingFilter (Lucene 2.9) that replaces accented characters by their unaccented equivalent (and uncommitted bug fix: https://issues.apache.org/jira/browse/LUCENE-1343?focusedCommentId=12858907&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_12858907).
    public static String stripAccents(final String input) {
        if(input == null) {
            return null;
        }
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");//$NON-NLS-1$
        final String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Note that this doesn't correctly remove ligatures...
        return pattern.matcher(decomposed).replaceAll("");//$NON-NLS-1$
    }

    // Equals
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two CharSequences, returning {@code true} if they represent
     * equal sequences of characters.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringToolkit.equals(null, null)   = true
     * StringToolkit.equals(null, "abc")  = false
     * StringToolkit.equals("abc", null)  = false
     * StringToolkit.equals("abc", "abc") = true
     * StringToolkit.equals("abc", "ABC") = false
     * </pre>
     *
     * @see Object#equals(Object)
     * @param cs1  the first CharSequence, may be {@code null}
     * @param cs2  the second CharSequence, may be {@code null}
     * @return {@code true} if the CharSequences are equal (case-sensitive), or both {@code null}
     * @since 3.0 Changed signature from equals(String, String) to equals(CharSequence, CharSequence)
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return CharSequenceToolkit.regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
    }

    /**
     * <p>Compares two CharSequences, returning {@code true} if they represent
     * equal sequences of characters, ignoring case.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered equal. Comparison is case insensitive.</p>
     *
     * <pre>
     * StringToolkit.equalsIgnoreCase(null, null)   = true
     * StringToolkit.equalsIgnoreCase(null, "abc")  = false
     * StringToolkit.equalsIgnoreCase("abc", null)  = false
     * StringToolkit.equalsIgnoreCase("abc", "abc") = true
     * StringToolkit.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1  the first CharSequence, may be null
     * @param str2  the second CharSequence, may be null
     * @return {@code true} if the CharSequence are equal, case insensitive, or
     *  both {@code null}
     * @since 3.0 Changed signature from equalsIgnoreCase(String, String) to equalsIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean equalsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else if (str1 == str2) {
            return true;
        } else if (str1.length() != str2.length()) {
            return false;
        } else {
            return CharSequenceToolkit.regionMatches(str1, true, 0, str2, 0, str1.length());
        }
    }

    // IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the first index within a CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(int, int)} if possible.</p>
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code INDEX_NOT_FOUND (-1)}.</p>
     *
     * <pre>
     * StringToolkit.indexOf(null, *)         = -1
     * StringToolkit.indexOf("", *)           = -1
     * StringToolkit.indexOf("aabaabaa", 'a') = 0
     * StringToolkit.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchChar  the character to find
     * @return the first index of the search character,
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from indexOf(String, int) to indexOf(CharSequence, int)
     */
    public static int indexOf(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.indexOf(seq, searchChar, 0);
    }

    /**
     * <p>Finds the first index within a CharSequence from a start position,
     * handling {@code null}.
     * This method uses {@link String#indexOf(int, int)} if possible.</p>
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code (INDEX_NOT_FOUND) -1}.
     * A negative start position is treated as zero.
     * A start position greater than the string length returns {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.indexOf(null, *, *)          = -1
     * StringToolkit.indexOf("", *, *)            = -1
     * StringToolkit.indexOf("aabaabaa", 'b', 0)  = 2
     * StringToolkit.indexOf("aabaabaa", 'b', 3)  = 5
     * StringToolkit.indexOf("aabaabaa", 'b', 9)  = -1
     * StringToolkit.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search character (always &ge; startPos),
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from indexOf(String, int, int) to indexOf(CharSequence, int, int)
     */
    public static int indexOf(final CharSequence seq, final int searchChar, final int startPos) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.indexOf(seq, searchChar, startPos);
    }

    /**
     * <p>Finds the first index within a CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String, int)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.indexOf(null, *)          = -1
     * StringToolkit.indexOf(*, null)          = -1
     * StringToolkit.indexOf("", "")           = 0
     * StringToolkit.indexOf("", *)            = -1 (except when * = "")
     * StringToolkit.indexOf("aabaabaa", "a")  = 0
     * StringToolkit.indexOf("aabaabaa", "b")  = 2
     * StringToolkit.indexOf("aabaabaa", "ab") = 1
     * StringToolkit.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @return the first index of the search CharSequence,
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from indexOf(String, String) to indexOf(CharSequence, CharSequence)
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.indexOf(seq, searchSeq, 0);
    }

    /**
     * <p>Finds the first index within a CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String, int)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position is treated as zero.
     * An empty ("") search CharSequence always matches.
     * A start position greater than the string length only matches
     * an empty search CharSequence.</p>
     *
     * <pre>
     * StringToolkit.indexOf(null, *, *)          = -1
     * StringToolkit.indexOf(*, null, *)          = -1
     * StringToolkit.indexOf("", "", 0)           = 0
     * StringToolkit.indexOf("", *, 0)            = -1 (except when * = "")
     * StringToolkit.indexOf("aabaabaa", "a", 0)  = 0
     * StringToolkit.indexOf("aabaabaa", "b", 0)  = 2
     * StringToolkit.indexOf("aabaabaa", "ab", 0) = 1
     * StringToolkit.indexOf("aabaabaa", "b", 3)  = 5
     * StringToolkit.indexOf("aabaabaa", "b", 9)  = -1
     * StringToolkit.indexOf("aabaabaa", "b", -1) = 2
     * StringToolkit.indexOf("aabaabaa", "", 2)   = 2
     * StringToolkit.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search CharSequence (always &ge; startPos),
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from indexOf(String, String, int) to indexOf(CharSequence, CharSequence, int)
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.indexOf(seq, searchSeq, startPos);
    }

    /**
     * <p>Finds the n-th index within a CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.ordinalIndexOf(null, *, *)          = -1
     * StringToolkit.ordinalIndexOf(*, null, *)          = -1
     * StringToolkit.ordinalIndexOf("", "", *)           = 0
     * StringToolkit.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringToolkit.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringToolkit.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringToolkit.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringToolkit.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringToolkit.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringToolkit.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringToolkit.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * <p>Note that 'head(CharSequence str, int n)' may be implemented as: </p>
     *
     * <pre>
     *   str.substring(0, lastOrdinalIndexOf(str, "\n", n))
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @param ordinal  the n-th {@code searchStr} to find
     * @return the n-th index of the search CharSequence,
     *  {@code -1} ({@code INDEX_NOT_FOUND}) if no match or {@code null} string input
     * @since 2.1
     * @since 3.0 Changed signature from ordinalIndexOf(String, String, int) to ordinalIndexOf(CharSequence, CharSequence, int)
     */
    public static int ordinalIndexOf(final CharSequence str, final CharSequence searchStr, final int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    /**
     * <p>Finds the n-th index within a String, handling {@code null}.
     * This method uses {@link String#indexOf(String)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.</p>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @param ordinal  the n-th {@code searchStr} to find
     * @param lastIndex true if lastOrdinalIndexOf() otherwise false if ordinalIndexOf()
     * @return the n-th index of the search CharSequence,
     *  {@code -1} ({@code INDEX_NOT_FOUND}) if no match or {@code null} string input
     */
    // Shared code between ordinalIndexOf(String,String,int) and lastOrdinalIndexOf(String,String,int)
    private static int ordinalIndexOf(final CharSequence str, final CharSequence searchStr, final int ordinal, final boolean lastIndex) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return lastIndex ? str.length() : 0;
        }
        int found = 0;
        int index = lastIndex ? str.length() : INDEX_NOT_FOUND;
        do {
            if (lastIndex) {
                index = CharSequenceToolkit.lastIndexOf(str, searchStr, index - searchStr.length());
            } else {
                index = CharSequenceToolkit.indexOf(str, searchStr, index + searchStr.length());
            }
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    /**
     * <p>Case in-sensitive find of the first index within a CharSequence.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position is treated as zero.
     * An empty ("") search CharSequence always matches.
     * A start position greater than the string length only matches
     * an empty search CharSequence.</p>
     *
     * <pre>
     * StringToolkit.indexOfIgnoreCase(null, *)          = -1
     * StringToolkit.indexOfIgnoreCase(*, null)          = -1
     * StringToolkit.indexOfIgnoreCase("", "")           = 0
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @return the first index of the search CharSequence,
     *  -1 if no match or {@code null} string input
     * @since 2.5
     * @since 3.0 Changed signature from indexOfIgnoreCase(String, String) to indexOfIgnoreCase(CharSequence, CharSequence)
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * <p>Case in-sensitive find of the first index within a CharSequence
     * from the specified position.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position is treated as zero.
     * An empty ("") search CharSequence always matches.
     * A start position greater than the string length only matches
     * an empty search CharSequence.</p>
     *
     * <pre>
     * StringToolkit.indexOfIgnoreCase(null, *, *)          = -1
     * StringToolkit.indexOfIgnoreCase(*, null, *)          = -1
     * StringToolkit.indexOfIgnoreCase("", "", 0)           = 0
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringToolkit.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringToolkit.indexOfIgnoreCase("abc", "", 9)        = 3
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search CharSequence (always &ge; startPos),
     *  -1 if no match or {@code null} string input
     * @since 2.5
     * @since 3.0 Changed signature from indexOfIgnoreCase(String, String, int) to indexOfIgnoreCase(CharSequence, CharSequence, int)
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        final int endLimit = str.length() - searchStr.length() + 1;
        if (startPos > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (CharSequenceToolkit.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    // LastIndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the last index within a CharSequence, handling {@code null}.
     * This method uses {@link String#lastIndexOf(int)} if possible.</p>
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.lastIndexOf(null, *)         = -1
     * StringToolkit.lastIndexOf("", *)           = -1
     * StringToolkit.lastIndexOf("aabaabaa", 'a') = 7
     * StringToolkit.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchChar  the character to find
     * @return the last index of the search character,
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from lastIndexOf(String, int) to lastIndexOf(CharSequence, int)
     */
    public static int lastIndexOf(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.lastIndexOf(seq, searchChar, seq.length());
    }

    /**
     * <p>Finds the last index within a CharSequence from a start position,
     * handling {@code null}.
     * This method uses {@link String#lastIndexOf(int, int)} if possible.</p>
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code -1}.
     * A negative start position returns {@code -1}.
     * A start position greater than the string length searches the whole string.
     * The search starts at the startPos and works backwards; matches starting after the start
     * position are ignored.
     * </p>
     *
     * <pre>
     * StringToolkit.lastIndexOf(null, *, *)          = -1
     * StringToolkit.lastIndexOf("", *,  *)           = -1
     * StringToolkit.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringToolkit.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringToolkit.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringToolkit.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringToolkit.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringToolkit.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position
     * @return the last index of the search character (always &le; startPos),
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from lastIndexOf(String, int, int) to lastIndexOf(CharSequence, int, int)
     */
    public static int lastIndexOf(final CharSequence seq, final int searchChar, final int startPos) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.lastIndexOf(seq, searchChar, startPos);
    }

    /**
     * <p>Finds the last index within a CharSequence, handling {@code null}.
     * This method uses {@link String#lastIndexOf(String)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.lastIndexOf(null, *)          = -1
     * StringToolkit.lastIndexOf(*, null)          = -1
     * StringToolkit.lastIndexOf("", "")           = 0
     * StringToolkit.lastIndexOf("aabaabaa", "a")  = 7
     * StringToolkit.lastIndexOf("aabaabaa", "b")  = 5
     * StringToolkit.lastIndexOf("aabaabaa", "ab") = 4
     * StringToolkit.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @return the last index of the search String,
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from lastIndexOf(String, String) to lastIndexOf(CharSequence, CharSequence)
     */
    public static int lastIndexOf(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.lastIndexOf(seq, searchSeq, seq.length());
    }

    /**
     * <p>Finds the n-th last index within a String, handling {@code null}.
     * This method uses {@link String#lastIndexOf(String)}.</p>
     *
     * <p>A {@code null} String will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.lastOrdinalIndexOf(null, *, *)          = -1
     * StringToolkit.lastOrdinalIndexOf(*, null, *)          = -1
     * StringToolkit.lastOrdinalIndexOf("", "", *)           = 0
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "a", 1)  = 7
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "a", 2)  = 6
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "b", 1)  = 5
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "b", 2)  = 2
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "ab", 1) = 4
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "ab", 2) = 1
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "", 1)   = 8
     * StringToolkit.lastOrdinalIndexOf("aabaabaa", "", 2)   = 8
     * </pre>
     *
     * <p>Note that 'tail(CharSequence str, int n)' may be implemented as: </p>
     *
     * <pre>
     *   str.substring(lastOrdinalIndexOf(str, "\n", n) + 1)
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @param ordinal  the n-th last {@code searchStr} to find
     * @return the n-th last index of the search CharSequence,
     *  {@code -1} ({@code INDEX_NOT_FOUND}) if no match or {@code null} string input
     * @since 2.5
     * @since 3.0 Changed signature from lastOrdinalIndexOf(String, String, int) to lastOrdinalIndexOf(CharSequence, CharSequence, int)
     */
    public static int lastOrdinalIndexOf(final CharSequence str, final CharSequence searchStr, final int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    /**
     * <p>Finds the last index within a CharSequence, handling {@code null}.
     * This method uses {@link String#lastIndexOf(String, int)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position returns {@code -1}.
     * An empty ("") search CharSequence always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.
     * The search starts at the startPos and works backwards; matches starting after the start
     * position are ignored.
     * </p>
     *
     * <pre>
     * StringToolkit.lastIndexOf(null, *, *)          = -1
     * StringToolkit.lastIndexOf(*, null, *)          = -1
     * StringToolkit.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringToolkit.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringToolkit.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringToolkit.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringToolkit.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringToolkit.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringToolkit.lastIndexOf("aabaabaa", "b", 0)  = -1
     * StringToolkit.lastIndexOf("aabaabaa", "b", 1)  = -1
     * StringToolkit.lastIndexOf("aabaabaa", "b", 2)  = 2
     * StringToolkit.lastIndexOf("aabaabaa", "ba", 2)  = -1
     * StringToolkit.lastIndexOf("aabaabaa", "ba", 2)  = 2
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the last index of the search CharSequence (always &le; startPos),
     *  -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from lastIndexOf(String, String, int) to lastIndexOf(CharSequence, CharSequence, int)
     */
    public static int lastIndexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceToolkit.lastIndexOf(seq, searchSeq, startPos);
    }

    /**
     * <p>Case in-sensitive find of the last index within a CharSequence.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position returns {@code -1}.
     * An empty ("") search CharSequence always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.</p>
     *
     * <pre>
     * StringToolkit.lastIndexOfIgnoreCase(null, *)          = -1
     * StringToolkit.lastIndexOfIgnoreCase(*, null)          = -1
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "A")  = 7
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "B")  = 5
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "AB") = 4
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @return the first index of the search CharSequence,
     *  -1 if no match or {@code null} string input
     * @since 2.5
     * @since 3.0 Changed signature from lastIndexOfIgnoreCase(String, String) to lastIndexOfIgnoreCase(CharSequence, CharSequence)
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    /**
     * <p>Case in-sensitive find of the last index within a CharSequence
     * from the specified position.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position returns {@code -1}.
     * An empty ("") search CharSequence always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.
     * The search starts at the startPos and works backwards; matches starting after the start
     * position are ignored.
     * </p>
     *
     * <pre>
     * StringToolkit.lastIndexOfIgnoreCase(null, *, *)          = -1
     * StringToolkit.lastIndexOfIgnoreCase(*, null, *)          = -1
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "A", 8)  = 7
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "B", 8)  = 5
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "AB", 8) = 4
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "B", 9)  = 5
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "B", -1) = -1
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringToolkit.lastIndexOfIgnoreCase("aabaabaa", "B", 0)  = -1
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @param startPos  the start position
     * @return the last index of the search CharSequence (always &le; startPos),
     *  -1 if no match or {@code null} input
     * @since 2.5
     * @since 3.0 Changed signature from lastIndexOfIgnoreCase(String, String, int) to lastIndexOfIgnoreCase(CharSequence, CharSequence, int)
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (startPos > str.length() - searchStr.length()) {
            startPos = str.length() - searchStr.length();
        }
        if (startPos < 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }

        for (int i = startPos; i >= 0; i--) {
            if (CharSequenceToolkit.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    // Contains
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if CharSequence contains a search character, handling {@code null}.
     * This method uses {@link String#indexOf(int)} if possible.</p>
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.contains(null, *)    = false
     * StringToolkit.contains("", *)      = false
     * StringToolkit.contains("abc", 'a') = true
     * StringToolkit.contains("abc", 'z') = false
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchChar  the character to find
     * @return true if the CharSequence contains the search character,
     *  false if not or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from contains(String, int) to contains(CharSequence, int)
     */
    public static boolean contains(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return false;
        }
        return CharSequenceToolkit.indexOf(seq, searchChar, 0) >= 0;
    }

    /**
     * <p>Checks if CharSequence contains a search CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.contains(null, *)     = false
     * StringToolkit.contains(*, null)     = false
     * StringToolkit.contains("", "")      = true
     * StringToolkit.contains("abc", "")   = true
     * StringToolkit.contains("abc", "a")  = true
     * StringToolkit.contains("abc", "z")  = false
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence,
     *  false if not or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from contains(String, String) to contains(CharSequence, CharSequence)
     */
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return false;
        }
        return CharSequenceToolkit.indexOf(seq, searchSeq, 0) >= 0;
    }

    /**
     * <p>Checks if CharSequence contains a search CharSequence irrespective of case,
     * handling {@code null}. Case-insensitivity is defined as by
     * {@link String#equalsIgnoreCase(String)}.
     *
     * <p>A {@code null} CharSequence will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.contains(null, *) = false
     * StringToolkit.contains(*, null) = false
     * StringToolkit.contains("", "") = true
     * StringToolkit.contains("abc", "") = true
     * StringToolkit.contains("abc", "a") = true
     * StringToolkit.contains("abc", "z") = false
     * StringToolkit.contains("abc", "A") = true
     * StringToolkit.contains("abc", "Z") = false
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence irrespective of
     * case or false if not or {@code null} string input
     * @since 3.0 Changed signature from containsIgnoreCase(String, String) to containsIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (CharSequenceToolkit.regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given CharSequence contains any whitespace characters.
     * @param seq the CharSequence to check (may be {@code null})
     * @return {@code true} if the CharSequence is not empty and
     * contains at least 1 whitespace character
     * @see Character#isWhitespace
     * @since 3.0
     */
    // From org.springframework.util.StringToolkit, under Apache License 2.0
    public static boolean containsWhitespace(final CharSequence seq) {
        if (isEmpty(seq)) {
            return false;
        }
        final int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // IndexOfAny chars
    //-----------------------------------------------------------------------
    /**
     * <p>Search a CharSequence to find the first index of any
     * character in the given set of characters.</p>
     *
     * <p>A {@code null} String will return {@code -1}.
     * A {@code null} or zero length search array will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.indexOfAny(null, *)                = -1
     * StringToolkit.indexOfAny("", *)                  = -1
     * StringToolkit.indexOfAny(*, null)                = -1
     * StringToolkit.indexOfAny(*, [])                  = -1
     * StringToolkit.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringToolkit.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringToolkit.indexOfAny("aba", ['z'])           = -1
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     * @since 3.0 Changed signature from indexOfAny(String, char[]) to indexOfAny(CharSequence, char...)
     */
    public static int indexOfAny(final CharSequence cs, final char... searchChars) {
        if (isEmpty(cs) || ObjectToolkit.isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        // ch is a supplementary character
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            return i;
                        }
                    } else {
                        return i;
                    }
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Search a CharSequence to find the first index of any
     * character in the given set of characters.</p>
     *
     * <p>A {@code null} String will return {@code -1}.
     * A {@code null} search string will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.indexOfAny(null, *)            = -1
     * StringToolkit.indexOfAny("", *)              = -1
     * StringToolkit.indexOfAny(*, null)            = -1
     * StringToolkit.indexOfAny(*, "")              = -1
     * StringToolkit.indexOfAny("zzabyycdxx", "za") = 0
     * StringToolkit.indexOfAny("zzabyycdxx", "by") = 3
     * StringToolkit.indexOfAny("aba","z")          = -1
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     * @since 3.0 Changed signature from indexOfAny(String, String) to indexOfAny(CharSequence, String)
     */
    public static int indexOfAny(final CharSequence cs, final String searchChars) {
        if (isEmpty(cs) || isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        return indexOfAny(cs, searchChars.toCharArray());
    }

    // ContainsAny
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if the CharSequence contains any character in the given
     * set of characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code false}.
     * A {@code null} or zero length search array will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.containsAny(null, *)                = false
     * StringToolkit.containsAny("", *)                  = false
     * StringToolkit.containsAny(*, null)                = false
     * StringToolkit.containsAny(*, [])                  = false
     * StringToolkit.containsAny("zzabyycdxx",['z','a']) = true
     * StringToolkit.containsAny("zzabyycdxx",['b','y']) = true
     * StringToolkit.containsAny("aba", ['z'])           = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the {@code true} if any of the chars are found,
     * {@code false} if no match or null input
     * @since 2.4
     * @since 3.0 Changed signature from containsAny(String, char[]) to containsAny(CharSequence, char...)
     */
    public static boolean containsAny(final CharSequence cs, final char... searchChars) {
        if (isEmpty(cs) || ObjectToolkit.isEmpty(searchChars)) {
            return false;
        }
        final int csLength = cs.length();
        final int searchLength = searchChars.length;
        final int csLast = csLength - 1;
        final int searchLast = searchLength - 1;
        for (int i = 0; i < csLength; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLength; j++) {
                if (searchChars[j] == ch) {
                    if (Character.isHighSurrogate(ch)) {
                        if (j == searchLast) {
                            // missing low surrogate, fine, like String.indexOf(String)
                            return true;
                        }
                        if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                            return true;
                        }
                    } else {
                        // ch is in the Basic Multilingual Plane
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Checks if the CharSequence contains any character in the given set of characters.
     * </p>
     *
     * <p>
     * A {@code null} CharSequence will return {@code false}. A {@code null} search CharSequence will return
     * {@code false}.
     * </p>
     *
     * <pre>
     * StringToolkit.containsAny(null, *)            = false
     * StringToolkit.containsAny("", *)              = false
     * StringToolkit.containsAny(*, null)            = false
     * StringToolkit.containsAny(*, "")              = false
     * StringToolkit.containsAny("zzabyycdxx", "za") = true
     * StringToolkit.containsAny("zzabyycdxx", "by") = true
     * StringToolkit.containsAny("aba","z")          = false
     * </pre>
     *
     * @param cs
     *            the CharSequence to check, may be null
     * @param searchChars
     *            the chars to search for, may be null
     * @return the {@code true} if any of the chars are found, {@code false} if no match or null input
     * @since 2.4
     * @since 3.0 Changed signature from containsAny(String, String) to containsAny(CharSequence, CharSequence)
     */
    public static boolean containsAny(final CharSequence cs, final CharSequence searchChars) {
        if (searchChars == null) {
            return false;
        }
        return containsAny(cs, CharSequenceToolkit.toCharArray(searchChars));
    }

    /**
     * <p>Checks if the CharSequence contains any of the CharSequences in the given array.</p>
     *
     * <p>
     * A {@code null} CharSequence will return {@code false}. A {@code null} or zero
     * length search array will return {@code false}.
     * </p>
     *
     * <pre>
     * StringToolkit.containsAny(null, *)            = false
     * StringToolkit.containsAny("", *)              = false
     * StringToolkit.containsAny(*, null)            = false
     * StringToolkit.containsAny(*, [])              = false
     * StringToolkit.containsAny("abcd", "ab", "cd") = false
     * StringToolkit.containsAny("abc", "d", "abc")  = true
     * </pre>
     *
     *
     * @param cs The CharSequence to check, may be null
     * @param searchCharSequences The array of CharSequences to search for, may be null
     * @return {@code true} if any of the search CharSequences are found, {@code false} otherwise
     * @since 3.4
     */
    public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
        if (isEmpty(cs) || ObjectToolkit.isEmpty(searchCharSequences)) {
            return false;
        }
        for (CharSequence searchCharSequence : searchCharSequences) {
            if (contains(cs, searchCharSequence)) {
                return true;
            }
        }
        return false;
    }

    // IndexOfAnyBut chars
    //-----------------------------------------------------------------------
    /**
     * <p>Searches a CharSequence to find the first index of any
     * character not in the given set of characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A {@code null} or zero length search array will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.indexOfAnyBut(null, *)                              = -1
     * StringToolkit.indexOfAnyBut("", *)                                = -1
     * StringToolkit.indexOfAnyBut(*, null)                              = -1
     * StringToolkit.indexOfAnyBut(*, [])                                = -1
     * StringToolkit.indexOfAnyBut("zzabyycdxx", new char[] {'z', 'a'} ) = 3
     * StringToolkit.indexOfAnyBut("aba", new char[] {'z'} )             = 0
     * StringToolkit.indexOfAnyBut("aba", new char[] {'a', 'b'} )        = -1

     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     * @since 3.0 Changed signature from indexOfAnyBut(String, char[]) to indexOfAnyBut(CharSequence, char...)
     */
    public static int indexOfAnyBut(final CharSequence cs, final char... searchChars) {
        if (isEmpty(cs) || ObjectToolkit.isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        outer:
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            continue outer;
                        }
                    } else {
                        continue outer;
                    }
                }
            }
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Search a CharSequence to find the first index of any
     * character not in the given set of characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A {@code null} or empty search string will return {@code -1}.</p>
     *
     * <pre>
     * StringToolkit.indexOfAnyBut(null, *)            = -1
     * StringToolkit.indexOfAnyBut("", *)              = -1
     * StringToolkit.indexOfAnyBut(*, null)            = -1
     * StringToolkit.indexOfAnyBut(*, "")              = -1
     * StringToolkit.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringToolkit.indexOfAnyBut("zzabyycdxx", "")   = -1
     * StringToolkit.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     * @since 3.0 Changed signature from indexOfAnyBut(String, String) to indexOfAnyBut(CharSequence, CharSequence)
     */
    public static int indexOfAnyBut(final CharSequence seq, final CharSequence searchChars) {
        if (isEmpty(seq) || isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            final char ch = seq.charAt(i);
            final boolean chFound = CharSequenceToolkit.indexOf(searchChars, ch, 0) >= 0;
            if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
                final char ch2 = seq.charAt(i + 1);
                if (chFound && CharSequenceToolkit.indexOf(searchChars, ch2, 0) < 0) {
                    return i;
                }
            } else {
                if (!chFound) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    // ContainsOnly
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if the CharSequence contains only certain characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code false}.
     * A {@code null} valid character array will return {@code false}.
     * An empty CharSequence (length()=0) always returns {@code true}.</p>
     *
     * <pre>
     * StringToolkit.containsOnly(null, *)       = false
     * StringToolkit.containsOnly(*, null)       = false
     * StringToolkit.containsOnly("", *)         = true
     * StringToolkit.containsOnly("ab", '')      = false
     * StringToolkit.containsOnly("abab", 'abc') = true
     * StringToolkit.containsOnly("ab1", 'abc')  = false
     * StringToolkit.containsOnly("abz", 'abc')  = false
     * </pre>
     *
     * @param cs  the String to check, may be null
     * @param valid  an array of valid chars, may be null
     * @return true if it only contains valid chars and is non-null
     * @since 3.0 Changed signature from containsOnly(String, char[]) to containsOnly(CharSequence, char...)
     */
    public static boolean containsOnly(final CharSequence cs, final char... valid) {
        // All these pre-checks are to maintain API with an older version
        if (valid == null || cs == null) {
            return false;
        }
        if (cs.length() == 0) {
            return true;
        }
        if (valid.length == 0) {
            return false;
        }
        return indexOfAnyBut(cs, valid) == INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the CharSequence contains only certain characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code false}.
     * A {@code null} valid character String will return {@code false}.
     * An empty String (length()=0) always returns {@code true}.</p>
     *
     * <pre>
     * StringToolkit.containsOnly(null, *)       = false
     * StringToolkit.containsOnly(*, null)       = false
     * StringToolkit.containsOnly("", *)         = true
     * StringToolkit.containsOnly("ab", "")      = false
     * StringToolkit.containsOnly("abab", "abc") = true
     * StringToolkit.containsOnly("ab1", "abc")  = false
     * StringToolkit.containsOnly("abz", "abc")  = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param validChars  a String of valid chars, may be null
     * @return true if it only contains valid chars and is non-null
     * @since 2.0
     * @since 3.0 Changed signature from containsOnly(String, String) to containsOnly(CharSequence, String)
     */
    public static boolean containsOnly(final CharSequence cs, final String validChars) {
        if (cs == null || validChars == null) {
            return false;
        }
        return containsOnly(cs, validChars.toCharArray());
    }

    // ContainsNone
    //-----------------------------------------------------------------------
    /**
     * <p>Checks that the CharSequence does not contain certain characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code true}.
     * A {@code null} invalid character array will return {@code true}.
     * An empty CharSequence (length()=0) always returns true.</p>
     *
     * <pre>
     * StringToolkit.containsNone(null, *)       = true
     * StringToolkit.containsNone(*, null)       = true
     * StringToolkit.containsNone("", *)         = true
     * StringToolkit.containsNone("ab", '')      = true
     * StringToolkit.containsNone("abab", 'xyz') = true
     * StringToolkit.containsNone("ab1", 'xyz')  = true
     * StringToolkit.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param searchChars  an array of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     * @since 3.0 Changed signature from containsNone(String, char[]) to containsNone(CharSequence, char...)
     */
    public static boolean containsNone(final CharSequence cs, final char... searchChars) {
        if (cs == null || searchChars == null) {
            return true;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (Character.isHighSurrogate(ch)) {
                        if (j == searchLast) {
                            // missing low surrogate, fine, like String.indexOf(String)
                            return false;
                        }
                        if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                            return false;
                        }
                    } else {
                        // ch is in the Basic Multilingual Plane
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>Checks that the CharSequence does not contain certain characters.</p>
     *
     * <p>A {@code null} CharSequence will return {@code true}.
     * A {@code null} invalid character array will return {@code true}.
     * An empty String ("") always returns true.</p>
     *
     * <pre>
     * StringToolkit.containsNone(null, *)       = true
     * StringToolkit.containsNone(*, null)       = true
     * StringToolkit.containsNone("", *)         = true
     * StringToolkit.containsNone("ab", "")      = true
     * StringToolkit.containsNone("abab", "xyz") = true
     * StringToolkit.containsNone("ab1", "xyz")  = true
     * StringToolkit.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @param invalidChars  a String of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     * @since 3.0 Changed signature from containsNone(String, String) to containsNone(CharSequence, String)
     */
    public static boolean containsNone(final CharSequence cs, final String invalidChars) {
        if (cs == null || invalidChars == null) {
            return true;
        }
        return containsNone(cs, invalidChars.toCharArray());
    }

    // IndexOfAny strings
    //-----------------------------------------------------------------------
    /**
     * <p>Find the first index of any of a set of potential substrings.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A {@code null} or zero length search array will return {@code -1}.
     * A {@code null} search array entry will be ignored, but a search
     * array containing "" will return {@code 0} if {@code str} is not
     * null. This method uses {@link String#indexOf(String)} if possible.</p>
     *
     * <pre>
     * StringToolkit.indexOfAny(null, *)                     = -1
     * StringToolkit.indexOfAny(*, null)                     = -1
     * StringToolkit.indexOfAny(*, [])                       = -1
     * StringToolkit.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * StringToolkit.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * StringToolkit.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * StringToolkit.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * StringToolkit.indexOfAny("zzabyycdxx", [""])          = 0
     * StringToolkit.indexOfAny("", [""])                    = 0
     * StringToolkit.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStrs  the CharSequences to search for, may be null
     * @return the first index of any of the searchStrs in str, -1 if no match
     * @since 3.0 Changed signature from indexOfAny(String, String[]) to indexOfAny(CharSequence, CharSequence...)
     */
    public static int indexOfAny(final CharSequence str, final CharSequence... searchStrs) {
        if (str == null || searchStrs == null) {
            return INDEX_NOT_FOUND;
        }
        final int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            final CharSequence search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = CharSequenceToolkit.indexOf(str, search, 0);
            if (tmp == INDEX_NOT_FOUND) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return ret == Integer.MAX_VALUE ? INDEX_NOT_FOUND : ret;
    }

    /**
     * <p>Find the latest index of any of a set of potential substrings.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A {@code null} search array will return {@code -1}.
     * A {@code null} or zero length search array entry will be ignored,
     * but a search array containing "" will return the length of {@code str}
     * if {@code str} is not null. This method uses {@link String#indexOf(String)} if possible</p>
     *
     * <pre>
     * StringToolkit.lastIndexOfAny(null, *)                   = -1
     * StringToolkit.lastIndexOfAny(*, null)                   = -1
     * StringToolkit.lastIndexOfAny(*, [])                     = -1
     * StringToolkit.lastIndexOfAny(*, [null])                 = -1
     * StringToolkit.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * StringToolkit.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * StringToolkit.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringToolkit.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringToolkit.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStrs  the CharSequences to search for, may be null
     * @return the last index of any of the CharSequences, -1 if no match
     * @since 3.0 Changed signature from lastIndexOfAny(String, String[]) to lastIndexOfAny(CharSequence, CharSequence)
     */
    public static int lastIndexOfAny(final CharSequence str, final CharSequence... searchStrs) {
        if (str == null || searchStrs == null) {
            return INDEX_NOT_FOUND;
        }
        final int sz = searchStrs.length;
        int ret = INDEX_NOT_FOUND;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            final CharSequence search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = CharSequenceToolkit.lastIndexOf(str, search, str.length());
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    // Substring
    //-----------------------------------------------------------------------
    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start {@code n}
     * characters from the end of the String.</p>
     *
     * <p>A {@code null} String will return {@code null}.
     * An empty ("") String will return "".</p>
     *
     * <pre>
     * StringToolkit.substring(null, *)   = null
     * StringToolkit.substring("", *)     = ""
     * StringToolkit.substring("abc", 0)  = "abc"
     * StringToolkit.substring("abc", 2)  = "c"
     * StringToolkit.substring("abc", 4)  = ""
     * StringToolkit.substring("abc", -2) = "bc"
     * StringToolkit.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position, {@code null} if null String input
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end {@code n}
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the {@code start}
     * position and ends before the {@code end} position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If {@code start} is not strictly to the left of {@code end}, ""
     * is returned.</p>
     *
     * <pre>
     * StringToolkit.substring(null, *, *)    = null
     * StringToolkit.substring("", * ,  *)    = "";
     * StringToolkit.substring("abc", 0, 2)   = "ab"
     * StringToolkit.substring("abc", 2, 0)   = ""
     * StringToolkit.substring("abc", 2, 4)   = "c"
     * StringToolkit.substring("abc", 4, 6)   = ""
     * StringToolkit.substring("abc", 2, 2)   = ""
     * StringToolkit.substring("abc", -2, -1) = "b"
     * StringToolkit.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end position,
     *  {@code null} if null String input
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // Left/Right/Mid
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the leftmost {@code len} characters of a String.</p>
     *
     * <p>If {@code len} characters are not available, or the
     * String is {@code null}, the String will be returned without
     * an exception. An empty String is returned if len is negative.</p>
     *
     * <pre>
     * StringToolkit.left(null, *)    = null
     * StringToolkit.left(*, -ve)     = ""
     * StringToolkit.left("", *)      = ""
     * StringToolkit.left("abc", 0)   = ""
     * StringToolkit.left("abc", 2)   = "ab"
     * StringToolkit.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  the String to get the leftmost characters from, may be null
     * @param len  the length of the required String
     * @return the leftmost characters, {@code null} if null String input
     */
    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * <p>Gets the rightmost {@code len} characters of a String.</p>
     *
     * <p>If {@code len} characters are not available, or the String
     * is {@code null}, the String will be returned without an
     * an exception. An empty String is returned if len is negative.</p>
     *
     * <pre>
     * StringToolkit.right(null, *)    = null
     * StringToolkit.right(*, -ve)     = ""
     * StringToolkit.right("", *)      = ""
     * StringToolkit.right("abc", 0)   = ""
     * StringToolkit.right("abc", 2)   = "bc"
     * StringToolkit.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  the String to get the rightmost characters from, may be null
     * @param len  the length of the required String
     * @return the rightmost characters, {@code null} if null String input
     */
    public static String right(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * <p>Gets {@code len} characters from the middle of a String.</p>
     *
     * <p>If {@code len} characters are not available, the remainder
     * of the String will be returned without an exception. If the
     * String is {@code null}, {@code null} will be returned.
     * An empty String is returned if len is negative or exceeds the
     * length of {@code str}.</p>
     *
     * <pre>
     * StringToolkit.mid(null, *, *)    = null
     * StringToolkit.mid(*, *, -ve)     = ""
     * StringToolkit.mid("", 0, *)      = ""
     * StringToolkit.mid("abc", 0, 2)   = "ab"
     * StringToolkit.mid("abc", 0, 4)   = "abc"
     * StringToolkit.mid("abc", 2, 4)   = "c"
     * StringToolkit.mid("abc", 4, 2)   = ""
     * StringToolkit.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the characters from, may be null
     * @param pos  the position to start from, negative treated as zero
     * @param len  the length of the required String
     * @return the middle characters, {@code null} if null String input
     */
    public static String mid(final String str, int pos, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringToolkit.substringBefore(null, *)      = null
     * StringToolkit.substringBefore("", *)        = ""
     * StringToolkit.substringBefore("abc", "a")   = ""
     * StringToolkit.substringBefore("abcba", "b") = "a"
     * StringToolkit.substringBefore("abc", "c")   = "ab"
     * StringToolkit.substringBefore("abc", "d")   = "abc"
     * StringToolkit.substringBefore("abc", "")    = ""
     * StringToolkit.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the empty string if the
     * input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringToolkit.substringAfter(null, *)      = null
     * StringToolkit.substringAfter("", *)        = ""
     * StringToolkit.substringAfter(*, null)      = ""
     * StringToolkit.substringAfter("abc", "a")   = "bc"
     * StringToolkit.substringAfter("abcba", "b") = "cba"
     * StringToolkit.substringAfter("abc", "c")   = ""
     * StringToolkit.substringAfter("abc", "d")   = ""
     * StringToolkit.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * <p>Gets the substring before the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringToolkit.substringBeforeLast(null, *)      = null
     * StringToolkit.substringBeforeLast("", *)        = ""
     * StringToolkit.substringBeforeLast("abcba", "b") = "abc"
     * StringToolkit.substringBeforeLast("abc", "c")   = "ab"
     * StringToolkit.substringBeforeLast("a", "a")     = ""
     * StringToolkit.substringBeforeLast("a", "z")     = "a"
     * StringToolkit.substringBeforeLast("a", null)    = "a"
     * StringToolkit.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the last occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(final String str, final String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the empty string if
     * the input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringToolkit.substringAfterLast(null, *)      = null
     * StringToolkit.substringAfterLast("", *)        = ""
     * StringToolkit.substringAfterLast(*, "")        = ""
     * StringToolkit.substringAfterLast(*, null)      = ""
     * StringToolkit.substringAfterLast("abc", "a")   = "bc"
     * StringToolkit.substringAfterLast("abcba", "b") = "a"
     * StringToolkit.substringAfterLast("abc", "c")   = ""
     * StringToolkit.substringAfterLast("a", "a")     = ""
     * StringToolkit.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfterLast(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    // Substring between
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} tag returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.substringBetween(null, *)            = null
     * StringToolkit.substringBetween("", "")             = ""
     * StringToolkit.substringBetween("", "tag")          = null
     * StringToolkit.substringBetween("tagabctag", null)  = null
     * StringToolkit.substringBetween("tagabctag", "")    = ""
     * StringToolkit.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param tag  the String before and after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(final String str, final String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open and close returns an empty string.</p>
     *
     * <pre>
     * StringToolkit.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringToolkit.substringBetween(null, *, *)          = null
     * StringToolkit.substringBetween(*, null, *)          = null
     * StringToolkit.substringBetween(*, *, null)          = null
     * StringToolkit.substringBetween("", "", "")          = ""
     * StringToolkit.substringBetween("", "", "]")         = null
     * StringToolkit.substringBetween("", "[", "]")        = null
     * StringToolkit.substringBetween("yabcz", "", "")     = ""
     * StringToolkit.substringBetween("yabcz", "y", "z")   = "abc"
     * StringToolkit.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close  the String after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * <p>Searches a String for substrings delimited by a start and end tag,
     * returning all matching substrings in an array.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open/close returns {@code null} (no match).</p>
     *
     * <pre>
     * StringToolkit.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
     * StringToolkit.substringsBetween(null, *, *)            = null
     * StringToolkit.substringsBetween(*, null, *)            = null
     * StringToolkit.substringsBetween(*, *, null)            = null
     * StringToolkit.substringsBetween("", "[", "]")          = []
     * </pre>
     *
     * @param str  the String containing the substrings, null returns null, empty returns empty
     * @param open  the String identifying the start of the substring, empty returns null
     * @param close  the String identifying the end of the substring, empty returns null
     * @return a String Array of substrings, or {@code null} if no match
     * @since 2.3
     */
    public static String[] substringsBetween(final String str, final String open, final String close) {
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == 0) {
            return new String[0];
        }
        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<String>();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.toArray(new String [list.size()]);
    }

    // Nested extraction
    //-----------------------------------------------------------------------

    // Splitting
    //-----------------------------------------------------------------------
    /**
     * <p>Splits the provided text into an array, using whitespace as the
     * separator.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.split(null)       = null
     * StringToolkit.split("")         = []
     * StringToolkit.split("abc def")  = ["abc", "def"]
     * StringToolkit.split("abc  def") = ["abc", "def"]
     * StringToolkit.split(" abc ")    = ["abc"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str) {
        return split(str, null, -1);
    }

    /**
     * <p>Splits the provided text into an array, separator specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.split(null, *)         = null
     * StringToolkit.split("", *)           = []
     * StringToolkit.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringToolkit.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringToolkit.split("a:b:c", '.')    = ["a:b:c"]
     * StringToolkit.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChar  the character used as the delimiter
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.0
     */
    public static String[] split(final String str, final char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    /**
     * <p>Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringToolkit.split(null, *)         = null
     * StringToolkit.split("", *)           = []
     * StringToolkit.split("abc def", null) = ["abc", "def"]
     * StringToolkit.split("abc def", " ")  = ["abc", "def"]
     * StringToolkit.split("abc  def", " ") = ["abc", "def"]
     * StringToolkit.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <p>If more than {@code max} delimited substrings are found, the last
     * returned string includes all characters after the first {@code max - 1}
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * StringToolkit.split(null, *, *)            = null
     * StringToolkit.split("", *, *)              = []
     * StringToolkit.split("ab cd ef", null, 0)   = ["ab", "cd", "ef"]
     * StringToolkit.split("ab   cd ef", null, 0) = ["ab", "cd", "ef"]
     * StringToolkit.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringToolkit.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  {@code null} splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars, final int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.</p>
     *
     * <p>The separator(s) will not be included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separator splits on whitespace.</p>
     *
     * <pre>
     * StringToolkit.splitByWholeSeparator(null, *)               = null
     * StringToolkit.splitByWholeSeparator("", *)                 = []
     * StringToolkit.splitByWholeSeparator("ab de fg", null)      = ["ab", "de", "fg"]
     * StringToolkit.splitByWholeSeparator("ab   de fg", null)    = ["ab", "de", "fg"]
     * StringToolkit.splitByWholeSeparator("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * StringToolkit.splitByWholeSeparator("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String was input
     */
    public static String[] splitByWholeSeparator(final String str, final String separator) {
        return splitByWholeSeparatorWorker( str, separator, -1, false ) ;
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.
     * Returns a maximum of {@code max} substrings.</p>
     *
     * <p>The separator(s) will not be included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separator splits on whitespace.</p>
     *
     * <pre>
     * StringToolkit.splitByWholeSeparator(null, *, *)               = null
     * StringToolkit.splitByWholeSeparator("", *, *)                 = []
     * StringToolkit.splitByWholeSeparator("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * StringToolkit.splitByWholeSeparator("ab   de fg", null, 0)    = ["ab", "de", "fg"]
     * StringToolkit.splitByWholeSeparator("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * StringToolkit.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * StringToolkit.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  {@code null} splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @return an array of parsed Strings, {@code null} if null String was input
     */
    public static String[] splitByWholeSeparator( final String str, final String separator, final int max ) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified. </p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separator splits on whitespace.</p>
     *
     * <pre>
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens(null, *)               = null
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("", *)                 = []
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab de fg", null)      = ["ab", "de", "fg"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null)    = ["ab", "", "", "de", "fg"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String was input
     * @since 2.4
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(final String str, final String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.
     * Returns a maximum of {@code max} substrings.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separator splits on whitespace.</p>
     *
     * <pre>
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens(null, *, *)               = null
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("", *, *)                 = []
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null, 0)    = ["ab", "", "", "de", "fg"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * StringToolkit.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  {@code null} splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @return an array of parsed Strings, {@code null} if null String was input
     * @since 2.4
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(final String str, final String separator, final int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    /**
     * Performs the logic for the {@code splitByWholeSeparatorPreserveAllTokens} methods.
     *
     * @param str  the String to parse, may be {@code null}
     * @param separator  String containing the String to be used as a delimiter,
     *  {@code null} splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are
     * treated as empty token separators; if {@code false}, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.4
     */
    private static String[] splitByWholeSeparatorWorker(
            final String str, final String separator, final int max, final boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }

        final int len = str.length();

        if (len == 0) {
            return new String[0];
        }

        if (separator == null || EMPTY.equals(separator)) {
            // Split on whitespace.
            return splitWorker(str, null, max, preserveAllTokens);
        }

        final int separatorLength = separator.length();

        final ArrayList<String> substrings = new ArrayList<String>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1;

                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end));

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength;
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    if (preserveAllTokens) {
                        numberOfSubstrings += 1;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add(EMPTY);
                        }
                    }
                    beg = end + separatorLength;
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg));
                end = len;
            }
        }

        return substrings.toArray(new String[substrings.size()]);
    }

    // -----------------------------------------------------------------------
    /**
     * <p>Splits the provided text into an array, using whitespace as the
     * separator, preserving all tokens, including empty tokens created by
     * adjacent separators. This is an alternative to using StringTokenizer.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.splitPreserveAllTokens(null)       = null
     * StringToolkit.splitPreserveAllTokens("")         = []
     * StringToolkit.splitPreserveAllTokens("abc def")  = ["abc", "def"]
     * StringToolkit.splitPreserveAllTokens("abc  def") = ["abc", "", "def"]
     * StringToolkit.splitPreserveAllTokens(" abc ")    = ["", "abc", ""]
     * </pre>
     *
     * @param str  the String to parse, may be {@code null}
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(final String str) {
        return splitWorker(str, null, -1, true);
    }

    /**
     * <p>Splits the provided text into an array, separator specified,
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.splitPreserveAllTokens(null, *)         = null
     * StringToolkit.splitPreserveAllTokens("", *)           = []
     * StringToolkit.splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
     * StringToolkit.splitPreserveAllTokens("a..b.c", '.')   = ["a", "", "b", "c"]
     * StringToolkit.splitPreserveAllTokens("a:b:c", '.')    = ["a:b:c"]
     * StringToolkit.splitPreserveAllTokens("a\tb\nc", null) = ["a", "b", "c"]
     * StringToolkit.splitPreserveAllTokens("a b c", ' ')    = ["a", "b", "c"]
     * StringToolkit.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", ""]
     * StringToolkit.splitPreserveAllTokens("a b c  ", ' ')   = ["a", "b", "c", "", ""]
     * StringToolkit.splitPreserveAllTokens(" a b c", ' ')   = ["", a", "b", "c"]
     * StringToolkit.splitPreserveAllTokens("  a b c", ' ')  = ["", "", a", "b", "c"]
     * StringToolkit.splitPreserveAllTokens(" a b c ", ' ')  = ["", a", "b", "c", ""]
     * </pre>
     *
     * @param str  the String to parse, may be {@code null}
     * @param separatorChar  the character used as the delimiter,
     *  {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(final String str, final char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    /**
     * Performs the logic for the {@code split} and
     * {@code splitPreserveAllTokens} methods that do not return a
     * maximum array length.
     *
     * @param str  the String to parse, may be {@code null}
     * @param separatorChar the separate character
     * @param preserveAllTokens if {@code true}, adjacent separators are
     * treated as empty token separators; if {@code false}, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(final String str, final char separatorChar, final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        final List<String> list = new ArrayList<String>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * <p>Splits the provided text into an array, separators specified,
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringToolkit.splitPreserveAllTokens(null, *)           = null
     * StringToolkit.splitPreserveAllTokens("", *)             = []
     * StringToolkit.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
     * StringToolkit.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
     * StringToolkit.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
     * StringToolkit.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
     * StringToolkit.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
     * StringToolkit.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
     * StringToolkit.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
     * StringToolkit.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
     * StringToolkit.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
     * StringToolkit.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
     * </pre>
     *
     * @param str  the String to parse, may be {@code null}
     * @param separatorChars  the characters used as the delimiters,
     *  {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    /**
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified, preserving all tokens, including empty tokens
     * created by adjacent separators.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <p>If more than {@code max} delimited substrings are found, the last
     * returned string includes all characters after the first {@code max - 1}
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * StringToolkit.splitPreserveAllTokens(null, *, *)            = null
     * StringToolkit.splitPreserveAllTokens("", *, *)              = []
     * StringToolkit.splitPreserveAllTokens("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * StringToolkit.splitPreserveAllTokens("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * StringToolkit.splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringToolkit.splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * StringToolkit.splitPreserveAllTokens("ab   de fg", null, 2) = ["ab", "  de fg"]
     * StringToolkit.splitPreserveAllTokens("ab   de fg", null, 3) = ["ab", "", " de fg"]
     * StringToolkit.splitPreserveAllTokens("ab   de fg", null, 4) = ["ab", "", "", "de fg"]
     * </pre>
     *
     * @param str  the String to parse, may be {@code null}
     * @param separatorChars  the characters used as the delimiters,
     *  {@code null} splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(final String str, final String separatorChars, final int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    /**
     * Performs the logic for the {@code split} and
     * {@code splitPreserveAllTokens} methods that return a maximum array
     * length.
     *
     * @param str  the String to parse, may be {@code null}
     * @param separatorChars the separate character
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are
     * treated as empty token separators; if {@code false}, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        final List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * <p>Splits a String by Character type as returned by
     * {@code java.lang.Character.getType(char)}. Groups of contiguous
     * characters of the same type are returned as complete tokens.
     * <pre>
     * StringToolkit.splitByCharacterType(null)         = null
     * StringToolkit.splitByCharacterType("")           = []
     * StringToolkit.splitByCharacterType("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringToolkit.splitByCharacterType("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringToolkit.splitByCharacterType("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringToolkit.splitByCharacterType("number5")    = ["number", "5"]
     * StringToolkit.splitByCharacterType("fooBar")     = ["foo", "B", "ar"]
     * StringToolkit.splitByCharacterType("foo200Bar")  = ["foo", "200", "B", "ar"]
     * StringToolkit.splitByCharacterType("ASFRules")   = ["ASFR", "ules"]
     * </pre>
     * @param str the String to split, may be {@code null}
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.4
     */
    public static String[] splitByCharacterType(final String str) {
        return splitByCharacterType(str, false);
    }

    /**
     * <p>Splits a String by Character type as returned by
     * {@code java.lang.Character.getType(char)}. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: the character of type
     * {@code Character.UPPERCASE_LETTER}, if any, immediately
     * preceding a token of type {@code Character.LOWERCASE_LETTER}
     * will belong to the following token rather than to the preceding, if any,
     * {@code Character.UPPERCASE_LETTER} token.
     * <pre>
     * StringToolkit.splitByCharacterTypeCamelCase(null)         = null
     * StringToolkit.splitByCharacterTypeCamelCase("")           = []
     * StringToolkit.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringToolkit.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringToolkit.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringToolkit.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
     * StringToolkit.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
     * StringToolkit.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
     * StringToolkit.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
     * </pre>
     * @param str the String to split, may be {@code null}
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.4
     */
    public static String[] splitByCharacterTypeCamelCase(final String str) {
        return splitByCharacterType(str, true);
    }

    /**
     * <p>Splits a String by Character type as returned by
     * {@code java.lang.Character.getType(char)}. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: if {@code camelCase} is {@code true},
     * the character of type {@code Character.UPPERCASE_LETTER}, if any,
     * immediately preceding a token of type {@code Character.LOWERCASE_LETTER}
     * will belong to the following token rather than to the preceding, if any,
     * {@code Character.UPPERCASE_LETTER} token.
     * @param str the String to split, may be {@code null}
     * @param camelCase whether to use so-called "camel-case" for letter types
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.4
     */
    private static String[] splitByCharacterType(final String str, final boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new String[0];
        }
        final char[] c = str.toCharArray();
        final List<String> list = new ArrayList<String>();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            final int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                final int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return list.toArray(new String[list.size()]);
    }

    // Joining
    //-----------------------------------------------------------------------
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No separator is added to the joined String.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringToolkit.join(null)            = null
     * StringToolkit.join([])              = ""
     * StringToolkit.join([null])          = ""
     * StringToolkit.join(["a", "b", "c"]) = "abc"
     * StringToolkit.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param <T> the specific type of values to join together
     * @param elements  the values to join together, may be null
     * @return the joined String, {@code null} if null array input
     * @since 2.0
     * @since 3.0 Changed signature to use varargs
     */
    public static <T> String join(final T... elements) {
        return join(elements, null);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringToolkit.join(["a", "b", "c"], null) = "abc"
     * StringToolkit.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 2.0
     */
    public static String join(final Object[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final long[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final int[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final short[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final byte[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final char[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final float[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final double[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }


    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringToolkit.join(["a", "b", "c"], null) = "abc"
     * StringToolkit.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from.  It is
     * an error to pass in an end index past the end of the array
     * @param endIndex the index to stop joining from (exclusive). It is
     * an error to pass in an end index past the end of the array
     * @return the joined String, {@code null} if null array input
     * @since 2.0
     */
    public static String join(final Object[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final long[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final int[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final byte[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final short[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final char[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final double[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringToolkit.join(null, *)               = null
     * StringToolkit.join([], *)                 = ""
     * StringToolkit.join([null], *)             = ""
     * StringToolkit.join([1, 2, 3], ';')  = "1;2;3"
     * StringToolkit.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in an end index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    public static String join(final float[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }


    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringToolkit.join(null, *)                = null
     * StringToolkit.join([], *)                  = ""
     * StringToolkit.join([null], *)              = ""
     * StringToolkit.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringToolkit.join(["a", "b", "c"], null)  = "abc"
     * StringToolkit.join(["a", "b", "c"], "")    = "abc"
     * StringToolkit.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringToolkit.join(null, *, *, *)                = null
     * StringToolkit.join([], *, *, *)                  = ""
     * StringToolkit.join([null], *, *, *)              = ""
     * StringToolkit.join(["a", "b", "c"], "--", 0, 3)  = "a--b--c"
     * StringToolkit.join(["a", "b", "c"], "--", 1, 3)  = "b--c"
     * StringToolkit.join(["a", "b", "c"], "--", 2, 3)  = "c"
     * StringToolkit.join(["a", "b", "c"], "--", 2, 2)  = ""
     * StringToolkit.join(["a", "b", "c"], null, 0, 3)  = "abc"
     * StringToolkit.join(["a", "b", "c"], "", 0, 3)    = "abc"
     * StringToolkit.join([null, "", "a"], ',', 0, 3)   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.
     * @param endIndex the index to stop joining from (exclusive).
     * @return the joined String, {@code null} if null array input; or the empty string
     * if {@code endIndex - startIndex <= 0}. The number of joined entries is given by
     * {@code endIndex - startIndex}
     * @throws ArrayIndexOutOfBoundsException ife<br>
     * {@code startIndex < 0} or <br>
     * {@code startIndex >= array.length()} or <br>
     * {@code endIndex < 0} or <br>
     * {@code endIndex > array.length()}
     */
    public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }

        final StringBuilder buf = new StringBuilder(noOfItems * 16);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <p>Joins the elements of the provided {@code Iterator} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list. Null objects or empty
     * strings within the iteration are represented by empty strings.</p>
     *
     * <p>See the examples here: {@link #join(Object[],char)}. </p>
     *
     * @param iterator  the {@code Iterator} of values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, {@code null} if null iterator input
     * @since 2.0
     */
    public static String join(final Iterator<?> iterator, final char separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            @SuppressWarnings( "deprecation" ) // ObjectToolkit.toString(Object) has been deprecated in 3.2
            final
            String result = ObjectToolkit.toString(first);
            return result;
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }

    /**
     * <p>Joins the elements of the provided {@code Iterator} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").</p>
     *
     * <p>See the examples here: {@link #join(Object[],String)}. </p>
     *
     * @param iterator  the {@code Iterator} of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            @SuppressWarnings( "deprecation" ) // ObjectToolkit.toString(Object) has been deprecated in 3.2
            final String result = ObjectToolkit.toString(first);
            return result;
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    /**
     * <p>Joins the elements of the provided {@code Iterable} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list. Null objects or empty
     * strings within the iteration are represented by empty strings.</p>
     *
     * <p>See the examples here: {@link #join(Object[],char)}. </p>
     *
     * @param iterable  the {@code Iterable} providing the values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, {@code null} if null iterator input
     * @since 2.3
     */
    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    /**
     * <p>Joins the elements of the provided {@code Iterable} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").</p>
     *
     * <p>See the examples here: {@link #join(Object[],String)}. </p>
     *
     * @param iterable  the {@code Iterable} providing the values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null iterator input
     * @since 2.3
     */
    public static String join(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    // Delete
    //-----------------------------------------------------------------------
    /**
     * <p>Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringToolkit.deleteWhitespace(null)         = null
     * StringToolkit.deleteWhitespace("")           = ""
     * StringToolkit.deleteWhitespace("abc")        = "abc"
     * StringToolkit.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str  the String to delete whitespace from, may be null
     * @return the String without whitespaces, {@code null} if null String input
     */
    public static String deleteWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    // Remove
    //-----------------------------------------------------------------------
    /**
     * <p>Removes a substring only if it is at the beginning of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} search string will return the source string.</p>
     *
     * <pre>
     * StringToolkit.removeStart(null, *)      = null
     * StringToolkit.removeStart("", *)        = ""
     * StringToolkit.removeStart(*, null)      = *
     * StringToolkit.removeStart("www.domain.com", "www.")   = "domain.com"
     * StringToolkit.removeStart("domain.com", "www.")       = "domain.com"
     * StringToolkit.removeStart("www.domain.com", "domain") = "www.domain.com"
     * StringToolkit.removeStart("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  {@code null} if null String input
     * @since 2.1
     */
    public static String removeStart(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)){
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Case insensitive removal of a substring if it is at the beginning of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} search string will return the source string.</p>
     *
     * <pre>
     * StringToolkit.removeStartIgnoreCase(null, *)      = null
     * StringToolkit.removeStartIgnoreCase("", *)        = ""
     * StringToolkit.removeStartIgnoreCase(*, null)      = *
     * StringToolkit.removeStartIgnoreCase("www.domain.com", "www.")   = "domain.com"
     * StringToolkit.removeStartIgnoreCase("www.domain.com", "WWW.")   = "domain.com"
     * StringToolkit.removeStartIgnoreCase("domain.com", "www.")       = "domain.com"
     * StringToolkit.removeStartIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * StringToolkit.removeStartIgnoreCase("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found,
     *  {@code null} if null String input
     * @since 2.4
     */
    public static String removeStartIgnoreCase(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (startsWithIgnoreCase(str, remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Removes a substring only if it is at the end of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} search string will return the source string.</p>
     *
     * <pre>
     * StringToolkit.removeEnd(null, *)      = null
     * StringToolkit.removeEnd("", *)        = ""
     * StringToolkit.removeEnd(*, null)      = *
     * StringToolkit.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * StringToolkit.removeEnd("www.domain.com", ".com")   = "www.domain"
     * StringToolkit.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * StringToolkit.removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  {@code null} if null String input
     * @since 2.1
     */
    public static String removeEnd(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * <p>Case insensitive removal of a substring if it is at the end of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} search string will return the source string.</p>
     *
     * <pre>
     * StringToolkit.removeEndIgnoreCase(null, *)      = null
     * StringToolkit.removeEndIgnoreCase("", *)        = ""
     * StringToolkit.removeEndIgnoreCase(*, null)      = *
     * StringToolkit.removeEndIgnoreCase("www.domain.com", ".com.")  = "www.domain.com"
     * StringToolkit.removeEndIgnoreCase("www.domain.com", ".com")   = "www.domain"
     * StringToolkit.removeEndIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * StringToolkit.removeEndIgnoreCase("abc", "")    = "abc"
     * StringToolkit.removeEndIgnoreCase("www.domain.com", ".COM") = "www.domain")
     * StringToolkit.removeEndIgnoreCase("www.domain.COM", ".com") = "www.domain")
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found,
     *  {@code null} if null String input
     * @since 2.4
     */
    public static String removeEndIgnoreCase(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (endsWithIgnoreCase(str, remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * <p>Removes all occurrences of a substring from within the source string.</p>
     *
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} remove string will return the source string.
     * An empty ("") remove string will return the source string.</p>
     *
     * <pre>
     * StringToolkit.remove(null, *)        = null
     * StringToolkit.remove("", *)          = ""
     * StringToolkit.remove(*, null)        = *
     * StringToolkit.remove(*, "")          = *
     * StringToolkit.remove("queued", "ue") = "qd"
     * StringToolkit.remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  {@code null} if null String input
     * @since 2.1
     */
    public static String remove(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, EMPTY, -1);
    }

    /**
     * <p>Removes all occurrences of a character from within the source string.</p>
     *
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.</p>
     *
     * <pre>
     * StringToolkit.remove(null, *)       = null
     * StringToolkit.remove("", *)         = ""
     * StringToolkit.remove("queued", 'u') = "qeed"
     * StringToolkit.remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the char to search for and remove, may be null
     * @return the substring with the char removed if found,
     *  {@code null} if null String input
     * @since 2.1
     */
    public static String remove(final String str, final char remove) {
        if (isEmpty(str) || str.indexOf(remove) == INDEX_NOT_FOUND) {
            return str;
        }
        final char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    // Replacing
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces a String with another String inside a larger String, once.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringToolkit.replaceOnce(null, *, *)        = null
     * StringToolkit.replaceOnce("", *, *)          = ""
     * StringToolkit.replaceOnce("any", null, *)    = "any"
     * StringToolkit.replaceOnce("any", *, null)    = "any"
     * StringToolkit.replaceOnce("any", "", *)      = "any"
     * StringToolkit.replaceOnce("aba", "a", null)  = "aba"
     * StringToolkit.replaceOnce("aba", "a", "")    = "ba"
     * StringToolkit.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @see #replace(String text, String searchString, String replacement, int max)
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replaceOnce(final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    /**
     * Replaces each substring of the source String that matches the given regular expression with the given
     * replacement using the {@link Pattern#DOTALL} option. DOTALL is also know as single-line mode in Perl. This call
     * is also equivalent to:
     * <ul>
     * <li>{@code source.replaceAll(&quot;(?s)&quot; + regex, replacement)}</li>
     * <li>{@code Pattern.compile(regex, Pattern.DOTALL).matcher(source).replaceAll(replacement)}</li>
     * </ul>
     *
     * @param source
     *            the source string
     * @param regex
     *            the regular expression to which this string is to be matched
     * @param replacement
     *            the string to be substituted for each match
     * @return The resulting {@code String}
     * @see String#replaceAll(String, String)
     * @see Pattern#DOTALL
     * @since 3.2
     */
    public static String replacePattern(final String source, final String regex, final String replacement) {
        return Pattern.compile(regex, Pattern.DOTALL).matcher(source).replaceAll(replacement);
    }

    /**
     * Removes each substring of the source String that matches the given regular expression using the DOTALL option.
     *
     * @param source
     *            the source string
     * @param regex
     *            the regular expression to which this string is to be matched
     * @return The resulting {@code String}
     * @see String#replaceAll(String, String)
     * @see Pattern#DOTALL
     * @since 3.2
     */
    public static String removePattern(final String source, final String regex) {
        return replacePattern(source, regex, StringToolkit.EMPTY);
    }

    /**
     * <p>Replaces all occurrences of a String within another String.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringToolkit.replace(null, *, *)        = null
     * StringToolkit.replace("", *, *)          = ""
     * StringToolkit.replace("any", null, *)    = "any"
     * StringToolkit.replace("any", *, null)    = "any"
     * StringToolkit.replace("any", "", *)      = "any"
     * StringToolkit.replace("aba", "a", null)  = "aba"
     * StringToolkit.replace("aba", "a", "")    = "b"
     * StringToolkit.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @see #replace(String text, String searchString, String replacement, int max)
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replace(final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first {@code max} values of the search String.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringToolkit.replace(null, *, *, *)         = null
     * StringToolkit.replace("", *, *, *)           = ""
     * StringToolkit.replace("any", null, *, *)     = "any"
     * StringToolkit.replace("any", *, null, *)     = "any"
     * StringToolkit.replace("any", "", *, *)       = "any"
     * StringToolkit.replace("any", *, *, 0)        = "any"
     * StringToolkit.replace("abaa", "a", null, -1) = "abaa"
     * StringToolkit.replace("abaa", "a", "", -1)   = "b"
     * StringToolkit.replace("abaa", "a", "z", 0)   = "abaa"
     * StringToolkit.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringToolkit.replace("abaa", "a", "z", 2)   = "zbza"
     * StringToolkit.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max  maximum number of values to replace, or {@code -1} if no maximum
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replace(final String text, final String searchString, final String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     *
     * <p>
     * A {@code null} reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored. This will not repeat. For repeating replaces, call the
     * overloaded method.
     * </p>
     *
     * <pre>
     *  StringToolkit.replaceEach(null, *, *)        = null
     *  StringToolkit.replaceEach("", *, *)          = ""
     *  StringToolkit.replaceEach("aba", null, null) = "aba"
     *  StringToolkit.replaceEach("aba", new String[0], null) = "aba"
     *  StringToolkit.replaceEach("aba", null, new String[0]) = "aba"
     *  StringToolkit.replaceEach("aba", new String[]{"a"}, null)  = "aba"
     *  StringToolkit.replaceEach("aba", new String[]{"a"}, new String[]{""})  = "b"
     *  StringToolkit.replaceEach("aba", new String[]{null}, new String[]{"a"})  = "aba"
     *  StringToolkit.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"})  = "wcte"
     *  (example of how it does not repeat)
     *  StringToolkit.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"})  = "dcte"
     * </pre>
     *
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, {@code null} if
     *         null String input
     * @throws IllegalArgumentException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    public static String replaceEach(final String text, final String[] searchList, final String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     *
     * <p>
     * A {@code null} reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored.
     * </p>
     *
     * <pre>
     *  StringToolkit.replaceEachRepeatedly(null, *, *) = null
     *  StringToolkit.replaceEachRepeatedly("", *, *) = ""
     *  StringToolkit.replaceEachRepeatedly("aba", null, null) = "aba"
     *  StringToolkit.replaceEachRepeatedly("aba", new String[0], null) = "aba"
     *  StringToolkit.replaceEachRepeatedly("aba", null, new String[0]) = "aba"
     *  StringToolkit.replaceEachRepeatedly("aba", new String[]{"a"}, null) = "aba"
     *  StringToolkit.replaceEachRepeatedly("aba", new String[]{"a"}, new String[]{""}) = "b"
     *  StringToolkit.replaceEachRepeatedly("aba", new String[]{null}, new String[]{"a"}) = "aba"
     *  StringToolkit.replaceEachRepeatedly("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}) = "wcte"
     *  (example of how it repeats)
     *  StringToolkit.replaceEachRepeatedly("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}) = "tcte"
     *  StringToolkit.replaceEachRepeatedly("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}) = IllegalStateException
     * </pre>
     *
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, {@code null} if
     *         null String input
     * @throws IllegalStateException
     *             if the search is repeating and there is an endless loop due
     *             to outputs of one being inputs to another
     * @throws IllegalArgumentException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    public static String replaceEachRepeatedly(final String text, final String[] searchList, final String[] replacementList) {
        // timeToLive should be 0 if not used or nothing to replace, else it's
        // the length of the replace array
        final int timeToLive = searchList == null ? 0 : searchList.length;
        return replaceEach(text, searchList, replacementList, true, timeToLive);
    }

    /**
     * <p>
     * Replace all occurrences of Strings within another String.
     * This is a private recursive helper method for {@link #replaceEachRepeatedly(String, String[], String[])} and
     * {@link #replaceEach(String, String[], String[])}
     * </p>
     *
     * <p>
     * A {@code null} reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored.
     * </p>
     *
     * <pre>
     *  StringToolkit.replaceEach(null, *, *, *, *) = null
     *  StringToolkit.replaceEach("", *, *, *, *) = ""
     *  StringToolkit.replaceEach("aba", null, null, *, *) = "aba"
     *  StringToolkit.replaceEach("aba", new String[0], null, *, *) = "aba"
     *  StringToolkit.replaceEach("aba", null, new String[0], *, *) = "aba"
     *  StringToolkit.replaceEach("aba", new String[]{"a"}, null, *, *) = "aba"
     *  StringToolkit.replaceEach("aba", new String[]{"a"}, new String[]{""}, *, >=0) = "b"
     *  StringToolkit.replaceEach("aba", new String[]{null}, new String[]{"a"}, *, >=0) = "aba"
     *  StringToolkit.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *, >=0) = "wcte"
     *  (example of how it repeats)
     *  StringToolkit.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false, >=0) = "dcte"
     *  StringToolkit.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true, >=2) = "tcte"
     *  StringToolkit.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, *, *) = IllegalStateException
     * </pre>
     *
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @param repeat if true, then replace repeatedly
     *       until there are no more possible replacements or timeToLive < 0
     * @param timeToLive
     *            if less than 0 then there is a circular reference and endless
     *            loop
     * @return the text with any replacements processed, {@code null} if
     *         null String input
     * @throws IllegalStateException
     *             if the search is repeating and there is an endless loop due
     *             to outputs of one being inputs to another
     * @throws IllegalArgumentException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    private static String replaceEach(
            final String text, final String[] searchList, final String[] replacementList, final boolean repeat, final int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null || text.isEmpty() || searchList == null ||
                searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - " +
                    "output of one loop is the input of another");
        }

        final int searchLength = searchList.length;
        final int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: "
                    + searchLength
                    + " vs "
                    + replacementLength);
        }

        // keep track of which still have matches
        final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                    searchList[i].isEmpty() || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            final int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        final StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                        searchList[i].isEmpty() || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        final int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        final String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces all occurrences of a character in a String with another.
     * This is a null-safe version of {@link String#replace(char, char)}.</p>
     *
     * <p>A {@code null} string input returns {@code null}.
     * An empty ("") string input returns an empty string.</p>
     *
     * <pre>
     * StringToolkit.replaceChars(null, *, *)        = null
     * StringToolkit.replaceChars("", *, *)          = ""
     * StringToolkit.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringToolkit.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChar  the character to search for, may be null
     * @param replaceChar  the character to replace, may be null
     * @return modified String, {@code null} if null string input
     * @since 2.0
     */
    public static String replaceChars(final String str, final char searchChar, final char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    /**
     * <p>Replaces multiple characters in a String in one go.
     * This method can also be used to delete characters.</p>
     *
     * <p>For example:<br>
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
     *
     * <p>A {@code null} string input returns {@code null}.
     * An empty ("") string input returns an empty string.
     * A null or empty set of search characters returns the input string.</p>
     *
     * <p>The length of the search characters should normally equal the length
     * of the replace characters.
     * If the search characters is longer, then the extra search characters
     * are deleted.
     * If the search characters is shorter, then the extra replace characters
     * are ignored.</p>
     *
     * <pre>
     * StringToolkit.replaceChars(null, *, *)           = null
     * StringToolkit.replaceChars("", *, *)             = ""
     * StringToolkit.replaceChars("abc", null, *)       = "abc"
     * StringToolkit.replaceChars("abc", "", *)         = "abc"
     * StringToolkit.replaceChars("abc", "b", null)     = "ac"
     * StringToolkit.replaceChars("abc", "b", "")       = "ac"
     * StringToolkit.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringToolkit.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringToolkit.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChars  a set of characters to search for, may be null
     * @param replaceChars  a set of characters to replace, may be null
     * @return modified String, {@code null} if null string input
     * @since 2.0
     */
    public static String replaceChars(final String str, final String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = EMPTY;
        }
        boolean modified = false;
        final int replaceCharsLength = replaceChars.length();
        final int strLength = str.length();
        final StringBuilder buf = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            final char ch = str.charAt(i);
            final int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    // Overlay
    //-----------------------------------------------------------------------
    /**
     * <p>Overlays part of a String with another String.</p>
     *
     * <p>A {@code null} string input returns {@code null}.
     * A negative index is treated as zero.
     * An index greater than the string length is treated as the string length.
     * The start index is always the smaller of the two indices.</p>
     *
     * <pre>
     * StringToolkit.overlay(null, *, *, *)            = null
     * StringToolkit.overlay("", "abc", 0, 0)          = "abc"
     * StringToolkit.overlay("abcdef", null, 2, 4)     = "abef"
     * StringToolkit.overlay("abcdef", "", 2, 4)       = "abef"
     * StringToolkit.overlay("abcdef", "", 4, 2)       = "abef"
     * StringToolkit.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringToolkit.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringToolkit.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringToolkit.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringToolkit.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringToolkit.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str  the String to do overlaying in, may be null
     * @param overlay  the String to overlay, may be null
     * @param start  the position to start overlaying at
     * @param end  the position to stop overlaying before
     * @return overlayed String, {@code null} if null String input
     * @since 2.0
     */
    public static String overlay(final String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = EMPTY;
        }
        final int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            final int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuilder(len + start - end + overlay.length() + 1)
                .append(str.substring(0, start))
                .append(overlay)
                .append(str.substring(end))
                .toString();
    }

    // Padding
    //-----------------------------------------------------------------------
    /**
     * <p>Repeat a String {@code repeat} times to form a
     * new String.</p>
     *
     * <pre>
     * StringToolkit.repeat(null, 2) = null
     * StringToolkit.repeat("", 0)   = ""
     * StringToolkit.repeat("", 2)   = ""
     * StringToolkit.repeat("a", 3)  = "aaa"
     * StringToolkit.repeat("ab", 2) = "abab"
     * StringToolkit.repeat("a", -2) = ""
     * </pre>
     *
     * @param str  the String to repeat, may be null
     * @param repeat  number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *  {@code null} if null String input
     */
    public static String repeat(final String str, final int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return repeat(str.charAt(0), repeat);
        }

        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1 :
                return repeat(str.charAt(0), repeat);
            case 2 :
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default :
                final StringBuilder buf = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * <p>Repeat a String {@code repeat} times to form a
     * new String, with a String separator injected each time. </p>
     *
     * <pre>
     * StringToolkit.repeat(null, null, 2) = null
     * StringToolkit.repeat(null, "x", 2)  = null
     * StringToolkit.repeat("", null, 0)   = ""
     * StringToolkit.repeat("", "", 2)     = ""
     * StringToolkit.repeat("", "x", 3)    = "xxx"
     * StringToolkit.repeat("?", ", ", 3)  = "?, ?, ?"
     * </pre>
     *
     * @param str        the String to repeat, may be null
     * @param separator  the String to inject, may be null
     * @param repeat     number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *  {@code null} if null String input
     * @since 2.5
     */
    public static String repeat(final String str, final String separator, final int repeat) {
        if(str == null || separator == null) {
            return repeat(str, repeat);
        }
        // given that repeat(String, int) is quite optimized, better to rely on it than try and splice this into it
        final String result = repeat(str + separator, repeat);
        return removeEnd(result, separator);
    }

    /**
     * <p>Returns padding using the specified delimiter repeated
     * to a given length.</p>
     *
     * <pre>
     * StringToolkit.repeat('e', 0)  = ""
     * StringToolkit.repeat('e', 3)  = "eee"
     * StringToolkit.repeat('e', -2) = ""
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with
     * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
     * as they require a pair of {@code char}s to be represented.
     * If you are needing to support full I18N of your applications
     * consider using {@link #repeat(String, int)} instead.
     * </p>
     *
     * @param ch  character to repeat
     * @param repeat  number of times to repeat char, negative treated as zero
     * @return String with repeated character
     * @see #repeat(String, int)
     */
    public static String repeat(final char ch, final int repeat) {
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    /**
     * <p>Right pad a String with spaces (' ').</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * <pre>
     * StringToolkit.rightPad(null, *)   = null
     * StringToolkit.rightPad("", 3)     = "   "
     * StringToolkit.rightPad("bat", 3)  = "bat"
     * StringToolkit.rightPad("bat", 5)  = "bat  "
     * StringToolkit.rightPad("bat", 1)  = "bat"
     * StringToolkit.rightPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return right padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * <p>Right pad a String with a specified character.</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * <pre>
     * StringToolkit.rightPad(null, *, *)     = null
     * StringToolkit.rightPad("", 3, 'z')     = "zzz"
     * StringToolkit.rightPad("bat", 3, 'z')  = "bat"
     * StringToolkit.rightPad("bat", 5, 'z')  = "batzz"
     * StringToolkit.rightPad("bat", 1, 'z')  = "bat"
     * StringToolkit.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return right padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * <pre>
     * StringToolkit.rightPad(null, *, *)      = null
     * StringToolkit.rightPad("", 3, "z")      = "zzz"
     * StringToolkit.rightPad("bat", 3, "yz")  = "bat"
     * StringToolkit.rightPad("bat", 5, "yz")  = "batyz"
     * StringToolkit.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringToolkit.rightPad("bat", 1, "yz")  = "bat"
     * StringToolkit.rightPad("bat", -1, "yz") = "bat"
     * StringToolkit.rightPad("bat", 5, null)  = "bat  "
     * StringToolkit.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>Left pad a String with spaces (' ').</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * <pre>
     * StringToolkit.leftPad(null, *)   = null
     * StringToolkit.leftPad("", 3)     = "   "
     * StringToolkit.leftPad("bat", 3)  = "bat"
     * StringToolkit.leftPad("bat", 5)  = "  bat"
     * StringToolkit.leftPad("bat", 1)  = "bat"
     * StringToolkit.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>Left pad a String with a specified character.</p>
     *
     * <p>Pad to a size of {@code size}.</p>
     *
     * <pre>
     * StringToolkit.leftPad(null, *, *)     = null
     * StringToolkit.leftPad("", 3, 'z')     = "zzz"
     * StringToolkit.leftPad("bat", 3, 'z')  = "bat"
     * StringToolkit.leftPad("bat", 5, 'z')  = "zzbat"
     * StringToolkit.leftPad("bat", 1, 'z')  = "bat"
     * StringToolkit.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     *
     * <p>Pad to a size of {@code size}.</p>
     *
     * <pre>
     * StringToolkit.leftPad(null, *, *)      = null
     * StringToolkit.leftPad("", 3, "z")      = "zzz"
     * StringToolkit.leftPad("bat", 3, "yz")  = "bat"
     * StringToolkit.leftPad("bat", 5, "yz")  = "yzbat"
     * StringToolkit.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringToolkit.leftPad("bat", 1, "yz")  = "bat"
     * StringToolkit.leftPad("bat", -1, "yz") = "bat"
     * StringToolkit.leftPad("bat", 5, null)  = "  bat"
     * StringToolkit.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs
     *            a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     *         {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    // Centering
    //-----------------------------------------------------------------------
    /**
     * <p>Centers a String in a larger String of size {@code size}
     * using the space character (' ').</p>
     *
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     *
     * <p>Equivalent to {@code center(str, size, " ")}.</p>
     *
     * <pre>
     * StringToolkit.center(null, *)   = null
     * StringToolkit.center("", 4)     = "    "
     * StringToolkit.center("ab", -1)  = "ab"
     * StringToolkit.center("ab", 4)   = " ab "
     * StringToolkit.center("abcd", 2) = "abcd"
     * StringToolkit.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @return centered String, {@code null} if null String input
     */
    public static String center(final String str, final int size) {
        return center(str, size, ' ');
    }

    /**
     * <p>Centers a String in a larger String of size {@code size}.
     * Uses a supplied character as the value to pad the String with.</p>
     *
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     *
     * <pre>
     * StringToolkit.center(null, *, *)     = null
     * StringToolkit.center("", 4, ' ')     = "    "
     * StringToolkit.center("ab", -1, ' ')  = "ab"
     * StringToolkit.center("ab", 4, ' ')   = " ab "
     * StringToolkit.center("abcd", 2, ' ') = "abcd"
     * StringToolkit.center("a", 4, ' ')    = " a  "
     * StringToolkit.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @param padChar  the character to pad the new String with
     * @return centered String, {@code null} if null String input
     * @since 2.0
     */
    public static String center(String str, final int size, final char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * <p>Centers a String in a larger String of size {@code size}.
     * Uses a supplied String as the value to pad the String with.</p>
     *
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     *
     * <pre>
     * StringToolkit.center(null, *, *)     = null
     * StringToolkit.center("", 4, " ")     = "    "
     * StringToolkit.center("ab", -1, " ")  = "ab"
     * StringToolkit.center("ab", 4, " ")   = " ab "
     * StringToolkit.center("abcd", 2, " ") = "abcd"
     * StringToolkit.center("a", 4, " ")    = " a  "
     * StringToolkit.center("a", 4, "yz")   = "yayz"
     * StringToolkit.center("abc", 7, null) = "  abc  "
     * StringToolkit.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @param padStr  the String to pad the new String with, must not be null or empty
     * @return centered String, {@code null} if null String input
     * @throws IllegalArgumentException if padStr is {@code null} or empty
     */
    public static String center(String str, final int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    // Case conversion
    //-----------------------------------------------------------------------
    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase()}.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.upperCase(null)  = null
     * StringToolkit.upperCase("")    = ""
     * StringToolkit.upperCase("aBc") = "ABC"
     * </pre>
     *
     * <p><strong>Note:</strong> As described in the documentation for {@link String#toUpperCase()},
     * the result of this method is affected by the current locale.
     * For platform-independent case transformations, the method {@link #lowerCase(String, Locale)}
     * should be used with a specific locale (e.g. {@link Locale#ENGLISH}).</p>
     *
     * @param str  the String to upper case, may be null
     * @return the upper cased String, {@code null} if null String input
     */
    public static String upperCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase(Locale)}.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.upperCase(null, Locale.ENGLISH)  = null
     * StringToolkit.upperCase("", Locale.ENGLISH)    = ""
     * StringToolkit.upperCase("aBc", Locale.ENGLISH) = "ABC"
     * </pre>
     *
     * @param str  the String to upper case, may be null
     * @param locale  the locale that defines the case transformation rules, must not be null
     * @return the upper cased String, {@code null} if null String input
     * @since 2.5
     */
    public static String upperCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(locale);
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase()}.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.lowerCase(null)  = null
     * StringToolkit.lowerCase("")    = ""
     * StringToolkit.lowerCase("aBc") = "abc"
     * </pre>
     *
     * <p><strong>Note:</strong> As described in the documentation for {@link String#toLowerCase()},
     * the result of this method is affected by the current locale.
     * For platform-independent case transformations, the method {@link #lowerCase(String, Locale)}
     * should be used with a specific locale (e.g. {@link Locale#ENGLISH}).</p>
     *
     * @param str  the String to lower case, may be null
     * @return the lower cased String, {@code null} if null String input
     */
    public static String lowerCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase(Locale)}.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.lowerCase(null, Locale.ENGLISH)  = null
     * StringToolkit.lowerCase("", Locale.ENGLISH)    = ""
     * StringToolkit.lowerCase("aBc", Locale.ENGLISH) = "abc"
     * </pre>
     *
     * @param str  the String to lower case, may be null
     * @param locale  the locale that defines the case transformation rules, must not be null
     * @return the lower cased String, {@code null} if null String input
     * @since 2.5
     */
    public static String lowerCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    /**
     * <p>Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
     *
     * <p>For a word based algorithm, see {@link org.apache.commons.lang3.text.WordUtils#capitalize(String)}.
     * A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.capitalize(null)  = null
     * StringToolkit.capitalize("")    = ""
     * StringToolkit.capitalize("cat") = "Cat"
     * StringToolkit.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     * @see org.apache.commons.lang3.text.WordUtils#capitalize(String)
     * @see #uncapitalize(String)
     * @since 2.0
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    /**
     * <p>Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.</p>
     *
     * <p>For a word based algorithm, see {@link org.apache.commons.lang3.text.WordUtils#uncapitalize(String)}.
     * A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.uncapitalize(null)  = null
     * StringToolkit.uncapitalize("")    = ""
     * StringToolkit.uncapitalize("Cat") = "cat"
     * StringToolkit.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str the String to uncapitalize, may be null
     * @return the uncapitalized String, {@code null} if null String input
     * @see org.apache.commons.lang3.text.WordUtils#uncapitalize(String)
     * @see #capitalize(String)
     * @since 2.0
     */
    public static String uncapitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            // already uncapitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toLowerCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    /**
     * <p>Swaps the case of a String changing upper and title case to
     * lower case, and lower case to upper case.</p>
     *
     * <ul>
     *  <li>Upper case character converts to Lower case</li>
     *  <li>Title case character converts to Lower case</li>
     *  <li>Lower case character converts to Upper case</li>
     * </ul>
     *
     * <p>For a word based algorithm, see {@link org.apache.commons.lang3.text.WordUtils#swapCase(String)}.
     * A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.swapCase(null)                 = null
     * StringToolkit.swapCase("")                   = ""
     * StringToolkit.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer performs a word based algorithm.
     * If you only use ASCII, you will notice no change.
     * That functionality is available in org.apache.commons.lang3.text.WordUtils.</p>
     *
     * @param str  the String to swap case, may be null
     * @return the changed String, {@code null} if null String input
     */
    public static String swapCase(final String str) {
        if (StringToolkit.isEmpty(str)) {
            return str;
        }

        final char[] buffer = str.toCharArray();

        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                buffer[i] = Character.toUpperCase(ch);
            }
        }
        return new String(buffer);
    }

    // Count matches
    //-----------------------------------------------------------------------
    /**
     * <p>Counts how many times the substring appears in the larger string.</p>
     *
     * <p>A {@code null} or empty ("") String input returns {@code 0}.</p>
     *
     * <pre>
     * StringToolkit.countMatches(null, *)       = 0
     * StringToolkit.countMatches("", *)         = 0
     * StringToolkit.countMatches("abba", null)  = 0
     * StringToolkit.countMatches("abba", "")    = 0
     * StringToolkit.countMatches("abba", "a")   = 2
     * StringToolkit.countMatches("abba", "ab")  = 1
     * StringToolkit.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param sub  the substring to count, may be null
     * @return the number of occurrences, 0 if either CharSequence is {@code null}
     * @since 3.0 Changed signature from countMatches(String, String) to countMatches(CharSequence, CharSequence)
     */
    public static int countMatches(final CharSequence str, final CharSequence sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = CharSequenceToolkit.indexOf(str, sub, idx)) != INDEX_NOT_FOUND) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * <p>Counts how many times the char appears in the given string.</p>
     *
     * <p>A {@code null} or empty ("") String input returns {@code 0}.</p>
     *
     * <pre>
     * StringToolkit.countMatches(null, *)       = 0
     * StringToolkit.countMatches("", *)         = 0
     * StringToolkit.countMatches("abba", 0)  = 0
     * StringToolkit.countMatches("abba", 'a')   = 2
     * StringToolkit.countMatches("abba", 'b')  = 2
     * StringToolkit.countMatches("abba", 'x') = 0
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param ch  the char to count
     * @return the number of occurrences, 0 if the CharSequence is {@code null}
     * @since 3.4
     */
    public static int countMatches(final CharSequence str, final char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        // We could also call str.toCharArray() for faster look ups but that would generate more garbage.
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    // Character Tests
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if the CharSequence contains only Unicode letters.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.isAlpha(null)   = false
     * StringToolkit.isAlpha("")     = false
     * StringToolkit.isAlpha("  ")   = false
     * StringToolkit.isAlpha("abc")  = true
     * StringToolkit.isAlpha("ab2c") = false
     * StringToolkit.isAlpha("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains letters, and is non-null
     * @since 3.0 Changed signature from isAlpha(String) to isAlpha(CharSequence)
     * @since 3.0 Changed "" to return false and not true
     */
    public static boolean isAlpha(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode letters and
     * space (' ').</p>
     *
     * <p>{@code null} will return {@code false}
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     *
     * <pre>
     * StringToolkit.isAlphaSpace(null)   = false
     * StringToolkit.isAlphaSpace("")     = true
     * StringToolkit.isAlphaSpace("  ")   = true
     * StringToolkit.isAlphaSpace("abc")  = true
     * StringToolkit.isAlphaSpace("ab c") = true
     * StringToolkit.isAlphaSpace("ab2c") = false
     * StringToolkit.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains letters and space,
     *  and is non-null
     * @since 3.0 Changed signature from isAlphaSpace(String) to isAlphaSpace(CharSequence)
     */
    public static boolean isAlphaSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode letters or digits.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.isAlphanumeric(null)   = false
     * StringToolkit.isAlphanumeric("")     = false
     * StringToolkit.isAlphanumeric("  ")   = false
     * StringToolkit.isAlphanumeric("abc")  = true
     * StringToolkit.isAlphanumeric("ab c") = false
     * StringToolkit.isAlphanumeric("ab2c") = true
     * StringToolkit.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains letters or digits,
     *  and is non-null
     * @since 3.0 Changed signature from isAlphanumeric(String) to isAlphanumeric(CharSequence)
     * @since 3.0 Changed "" to return false and not true
     */
    public static boolean isAlphanumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode letters, digits
     * or space ({@code ' '}).</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     *
     * <pre>
     * StringToolkit.isAlphanumericSpace(null)   = false
     * StringToolkit.isAlphanumericSpace("")     = true
     * StringToolkit.isAlphanumericSpace("  ")   = true
     * StringToolkit.isAlphanumericSpace("abc")  = true
     * StringToolkit.isAlphanumericSpace("ab c") = true
     * StringToolkit.isAlphanumericSpace("ab2c") = true
     * StringToolkit.isAlphanumericSpace("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains letters, digits or space,
     *  and is non-null
     * @since 3.0 Changed signature from isAlphanumericSpace(String) to isAlphanumericSpace(CharSequence)
     */
    public static boolean isAlphanumericSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }


    /**
     * <p>Checks if the CharSequence contains only Unicode digits.
     * A decimal point is not a Unicode digit and returns false.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <p>Note that the method does not allow for a leading sign, either positive or negative.
     * Also, if a String passes the numeric test, it may still generate a NumberFormatException
     * when parsed by Integer.parseInt or Long.parseLong, e.g. if the value is outside the range
     * for int or long respectively.</p>
     *
     * <pre>
     * StringToolkit.isNumeric(null)   = false
     * StringToolkit.isNumeric("")     = false
     * StringToolkit.isNumeric("  ")   = false
     * StringToolkit.isNumeric("123")  = true
     * StringToolkit.isNumeric("\u0967\u0968\u0969")  = true
     * StringToolkit.isNumeric("12 3") = false
     * StringToolkit.isNumeric("ab2c") = false
     * StringToolkit.isNumeric("12-3") = false
     * StringToolkit.isNumeric("12.3") = false
     * StringToolkit.isNumeric("-123") = false
     * StringToolkit.isNumeric("+123") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains digits, and is non-null
     * @since 3.0 Changed signature from isNumeric(String) to isNumeric(CharSequence)
     * @since 3.0 Changed "" to return false and not true
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode digits or space
     * ({@code ' '}).
     * A decimal point is not a Unicode digit and returns false.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     *
     * <pre>
     * StringToolkit.isNumericSpace(null)   = false
     * StringToolkit.isNumericSpace("")     = true
     * StringToolkit.isNumericSpace("  ")   = true
     * StringToolkit.isNumericSpace("123")  = true
     * StringToolkit.isNumericSpace("12 3") = true
     * StringToolkit.isNumeric("\u0967\u0968\u0969")  = true
     * StringToolkit.isNumeric("\u0967\u0968 \u0969")  = true
     * StringToolkit.isNumericSpace("ab2c") = false
     * StringToolkit.isNumericSpace("12-3") = false
     * StringToolkit.isNumericSpace("12.3") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains digits or space,
     *  and is non-null
     * @since 3.0 Changed signature from isNumericSpace(String) to isNumericSpace(CharSequence)
     */
    public static boolean isNumericSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only whitespace.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     *
     * <pre>
     * StringToolkit.isWhitespace(null)   = false
     * StringToolkit.isWhitespace("")     = true
     * StringToolkit.isWhitespace("  ")   = true
     * StringToolkit.isWhitespace("abc")  = false
     * StringToolkit.isWhitespace("ab2c") = false
     * StringToolkit.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains whitespace, and is non-null
     * @since 2.0
     * @since 3.0 Changed signature from isWhitespace(String) to isWhitespace(CharSequence)
     */
    public static boolean isWhitespace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only lowercase characters.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.isAllLowerCase(null)   = false
     * StringToolkit.isAllLowerCase("")     = false
     * StringToolkit.isAllLowerCase("  ")   = false
     * StringToolkit.isAllLowerCase("abc")  = true
     * StringToolkit.isAllLowerCase("abC")  = false
     * StringToolkit.isAllLowerCase("ab c") = false
     * StringToolkit.isAllLowerCase("ab1c") = false
     * StringToolkit.isAllLowerCase("ab/c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains lowercase characters, and is non-null
     * @since 2.5
     * @since 3.0 Changed signature from isAllLowerCase(String) to isAllLowerCase(CharSequence)
     */
    public static boolean isAllLowerCase(final CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLowerCase(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only uppercase characters.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty String (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringToolkit.isAllUpperCase(null)   = false
     * StringToolkit.isAllUpperCase("")     = false
     * StringToolkit.isAllUpperCase("  ")   = false
     * StringToolkit.isAllUpperCase("ABC")  = true
     * StringToolkit.isAllUpperCase("aBC")  = false
     * StringToolkit.isAllUpperCase("A C")  = false
     * StringToolkit.isAllUpperCase("A1C")  = false
     * StringToolkit.isAllUpperCase("A/C")  = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains uppercase characters, and is non-null
     * @since 2.5
     * @since 3.0 Changed signature from isAllUpperCase(String) to isAllUpperCase(CharSequence)
     */
    public static boolean isAllUpperCase(final CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isUpperCase(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    //-----------------------------------------------------------------------
    /**
     * <p>Returns either the passed in String,
     * or if the String is {@code null}, an empty String ("").</p>
     *
     * <pre>
     * StringToolkit.defaultString(null)  = ""
     * StringToolkit.defaultString("")    = ""
     * StringToolkit.defaultString("bat") = "bat"
     * </pre>
     *
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @return the passed in String, or the empty String if it
     *  was {@code null}
     */
    public static String defaultString(final String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * <p>Returns either the passed in String, or if the String is
     * {@code null}, the value of {@code defaultStr}.</p>
     *
     * <pre>
     * StringToolkit.defaultString(null, "NULL")  = "NULL"
     * StringToolkit.defaultString("", "NULL")    = ""
     * StringToolkit.defaultString("bat", "NULL") = "bat"
     * </pre>
     *
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @param defaultStr  the default String to return
     *  if the input is {@code null}, may be null
     * @return the passed in String, or the default if it was {@code null}
     */
    public static String defaultString(final String str, final String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * whitespace, empty ("") or {@code null}, the value of {@code defaultStr}.</p>
     *
     * <pre>
     * StringToolkit.defaultIfBlank(null, "NULL")  = "NULL"
     * StringToolkit.defaultIfBlank("", "NULL")    = "NULL"
     * StringToolkit.defaultIfBlank(" ", "NULL")   = "NULL"
     * StringToolkit.defaultIfBlank("bat", "NULL") = "bat"
     * StringToolkit.defaultIfBlank("", null)      = null
     * </pre>
     * @param <T> the specific kind of CharSequence
     * @param str the CharSequence to check, may be null
     * @param defaultStr  the default CharSequence to return
     *  if the input is whitespace, empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * empty or {@code null}, the value of {@code defaultStr}.</p>
     *
     * <pre>
     * StringToolkit.defaultIfEmpty(null, "NULL")  = "NULL"
     * StringToolkit.defaultIfEmpty("", "NULL")    = "NULL"
     * StringToolkit.defaultIfEmpty(" ", "NULL")   = " "
     * StringToolkit.defaultIfEmpty("bat", "NULL") = "bat"
     * StringToolkit.defaultIfEmpty("", null)      = null
     * </pre>
     * @param <T> the specific kind of CharSequence
     * @param str  the CharSequence to check, may be null
     * @param defaultStr  the default CharSequence to return
     *  if the input is empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     */
    public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    // Reversing
    //-----------------------------------------------------------------------
    /**
     * <p>Reverses a String as per {@link StringBuilder#reverse()}.</p>
     *
     * <p>A {@code null} String returns {@code null}.</p>
     *
     * <pre>
     * StringToolkit.reverse(null)  = null
     * StringToolkit.reverse("")    = ""
     * StringToolkit.reverse("bat") = "tab"
     * </pre>
     *
     * @param str  the String to reverse, may be null
     * @return the reversed String, {@code null} if null String input
     */
    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }



    // Abbreviating
    //-----------------------------------------------------------------------
    /**
     * <p>Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "Now is the time for..."</p>
     *
     * <p>Specifically:</p>
     * <ul>
     *   <li>If the number of characters in {@code str} is less than or equal to
     *       {@code maxWidth}, return {@code str}.</li>
     *   <li>Else abbreviate it to {@code (substring(str, 0, max-3) + "...")}.</li>
     *   <li>If {@code maxWidth} is less than {@code 4}, throw an
     *       {@code IllegalArgumentException}.</li>
     *   <li>In no case will it return a String of length greater than
     *       {@code maxWidth}.</li>
     * </ul>
     *
     * <pre>
     * StringToolkit.abbreviate(null, *)      = null
     * StringToolkit.abbreviate("", 4)        = ""
     * StringToolkit.abbreviate("abcdefg", 6) = "abc..."
     * StringToolkit.abbreviate("abcdefg", 7) = "abcdefg"
     * StringToolkit.abbreviate("abcdefg", 8) = "abcdefg"
     * StringToolkit.abbreviate("abcdefg", 4) = "a..."
     * StringToolkit.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param maxWidth  maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(final String str, final int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * <p>Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "...is the time for..."</p>
     *
     * <p>Works like {@code abbreviate(String, int)}, but allows you to specify
     * a "left edge" offset.  Note that this left edge is not necessarily going to
     * be the leftmost character in the result, or the first character following the
     * ellipses, but it will appear somewhere in the result.
     *
     * <p>In no case will it return a String of length greater than
     * {@code maxWidth}.</p>
     *
     * <pre>
     * StringToolkit.abbreviate(null, *, *)                = null
     * StringToolkit.abbreviate("", 0, 4)                  = ""
     * StringToolkit.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringToolkit.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringToolkit.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringToolkit.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringToolkit.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringToolkit.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringToolkit.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringToolkit.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringToolkit.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringToolkit.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringToolkit.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param offset  left edge of source String
     * @param maxWidth  maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(final String str, int offset, final int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }
        final String abrevMarker = "...";
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + abrevMarker;
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + maxWidth - 3 < str.length()) {
            return abrevMarker + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return abrevMarker + str.substring(str.length() - (maxWidth - 3));
    }

    /**
     * <p>Abbreviates a String to the length passed, replacing the middle characters with the supplied
     * replacement String.</p>
     *
     * <p>This abbreviation only occurs if the following criteria is met:</p>
     * <ul>
     * <li>Neither the String for abbreviation nor the replacement String are null or empty </li>
     * <li>The length to truncate to is less than the length of the supplied String</li>
     * <li>The length to truncate to is greater than 0</li>
     * <li>The abbreviated String will have enough room for the length supplied replacement String
     * and the first and last characters of the supplied String for abbreviation</li>
     * </ul>
     * <p>Otherwise, the returned String will be the same as the supplied String for abbreviation.
     * </p>
     *
     * <pre>
     * StringToolkit.abbreviateMiddle(null, null, 0)      = null
     * StringToolkit.abbreviateMiddle("abc", null, 0)      = "abc"
     * StringToolkit.abbreviateMiddle("abc", ".", 0)      = "abc"
     * StringToolkit.abbreviateMiddle("abc", ".", 3)      = "abc"
     * StringToolkit.abbreviateMiddle("abcdef", ".", 4)     = "ab.f"
     * </pre>
     *
     * @param str  the String to abbreviate, may be null
     * @param middle the String to replace the middle characters with, may be null
     * @param length the length to abbreviate {@code str} to.
     * @return the abbreviated String if the above criteria is met, or the original String supplied for abbreviation.
     * @since 2.5
     */
    public static String abbreviateMiddle(final String str, final String middle, final int length) {
        if (isEmpty(str) || isEmpty(middle)) {
            return str;
        }

        if (length >= str.length() || length < middle.length()+2) {
            return str;
        }

        final int targetSting = length-middle.length();
        final int startOffset = targetSting/2+targetSting%2;
        final int endOffset = str.length()-targetSting/2;

        final StringBuilder builder = new StringBuilder(length);
        builder.append(str.substring(0,startOffset));
        builder.append(middle);
        builder.append(str.substring(endOffset));

        return builder.toString();
    }

    // Difference
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two Strings, and returns the portion where they differ.
     * More precisely, return the remainder of the second String,
     * starting from where it's different from the first. This means that
     * the difference between "abc" and "ab" is the empty String and not "c". </p>
     *
     * <p>For example,
     * {@code difference("i am a machine", "i am a robot") -> "robot"}.</p>
     *
     * <pre>
     * StringToolkit.difference(null, null) = null
     * StringToolkit.difference("", "") = ""
     * StringToolkit.difference("", "abc") = "abc"
     * StringToolkit.difference("abc", "") = ""
     * StringToolkit.difference("abc", "abc") = ""
     * StringToolkit.difference("abc", "ab") = ""
     * StringToolkit.difference("ab", "abxyz") = "xyz"
     * StringToolkit.difference("abcde", "abxyz") = "xyz"
     * StringToolkit.difference("abcde", "xyz") = "xyz"
     * </pre>
     *
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return the portion of str2 where it differs from str1; returns the
     * empty String if they are equal
     * @see #indexOfDifference(CharSequence,CharSequence)
     * @since 2.0
     */
    public static String difference(final String str1, final String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        final int at = indexOfDifference(str1, str2);
        if (at == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * <p>Compares two CharSequences, and returns the index at which the
     * CharSequences begin to differ.</p>
     *
     * <p>For example,
     * {@code indexOfDifference("i am a machine", "i am a robot") -> 7}</p>
     *
     * <pre>
     * StringToolkit.indexOfDifference(null, null) = -1
     * StringToolkit.indexOfDifference("", "") = -1
     * StringToolkit.indexOfDifference("", "abc") = 0
     * StringToolkit.indexOfDifference("abc", "") = 0
     * StringToolkit.indexOfDifference("abc", "abc") = -1
     * StringToolkit.indexOfDifference("ab", "abxyz") = 2
     * StringToolkit.indexOfDifference("abcde", "abxyz") = 2
     * StringToolkit.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param cs1  the first CharSequence, may be null
     * @param cs2  the second CharSequence, may be null
     * @return the index where cs1 and cs2 begin to differ; -1 if they are equal
     * @since 2.0
     * @since 3.0 Changed signature from indexOfDifference(String, String) to
     * indexOfDifference(CharSequence, CharSequence)
     */
    public static int indexOfDifference(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return INDEX_NOT_FOUND;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Compares all CharSequences in an array and returns the index at which the
     * CharSequences begin to differ.</p>
     *
     * <p>For example,
     * <code>indexOfDifference(new String[] {"i am a machine", "i am a robot"}) -&gt; 7</code></p>
     *
     * <pre>
     * StringToolkit.indexOfDifference(null) = -1
     * StringToolkit.indexOfDifference(new String[] {}) = -1
     * StringToolkit.indexOfDifference(new String[] {"abc"}) = -1
     * StringToolkit.indexOfDifference(new String[] {null, null}) = -1
     * StringToolkit.indexOfDifference(new String[] {"", ""}) = -1
     * StringToolkit.indexOfDifference(new String[] {"", null}) = 0
     * StringToolkit.indexOfDifference(new String[] {"abc", null, null}) = 0
     * StringToolkit.indexOfDifference(new String[] {null, null, "abc"}) = 0
     * StringToolkit.indexOfDifference(new String[] {"", "abc"}) = 0
     * StringToolkit.indexOfDifference(new String[] {"abc", ""}) = 0
     * StringToolkit.indexOfDifference(new String[] {"abc", "abc"}) = -1
     * StringToolkit.indexOfDifference(new String[] {"abc", "a"}) = 1
     * StringToolkit.indexOfDifference(new String[] {"ab", "abxyz"}) = 2
     * StringToolkit.indexOfDifference(new String[] {"abcde", "abxyz"}) = 2
     * StringToolkit.indexOfDifference(new String[] {"abcde", "xyz"}) = 0
     * StringToolkit.indexOfDifference(new String[] {"xyz", "abcde"}) = 0
     * StringToolkit.indexOfDifference(new String[] {"i am a machine", "i am a robot"}) = 7
     * </pre>
     *
     * @param css  array of CharSequences, entries may be null
     * @return the index where the strings begin to differ; -1 if they are all equal
     * @since 2.4
     * @since 3.0 Changed signature from indexOfDifference(String...) to indexOfDifference(CharSequence...)
     */
    public static int indexOfDifference(final CharSequence... css) {
        if (css == null || css.length <= 1) {
            return INDEX_NOT_FOUND;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        final int arrayLen = css.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;

        // find the min and max string lengths; this avoids checking to make
        // sure we are not exceeding the length of the string each time through
        // the bottom loop.
        for (int i = 0; i < arrayLen; i++) {
            if (css[i] == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(css[i].length(), shortestStrLen);
                longestStrLen = Math.max(css[i].length(), longestStrLen);
            }
        }

        // handle lists containing all nulls or all empty strings
        if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
            return INDEX_NOT_FOUND;
        }

        // handle lists containing some nulls or some empty strings
        if (shortestStrLen == 0) {
            return 0;
        }

        // find the position with the first difference across all strings
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            final char comparisonChar = css[0].charAt(stringPos);
            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                if (css[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }

        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            // we compared all of the characters up to the length of the
            // shortest string and didn't find a match, but the string lengths
            // vary, so return the length of the shortest string.
            return shortestStrLen;
        }
        return firstDiff;
    }

    /**
     * <p>Compares all Strings in an array and returns the initial sequence of
     * characters that is common to all of them.</p>
     *
     * <p>For example,
     * <code>getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) -&gt; "i am a "</code></p>
     *
     * <pre>
     * StringToolkit.getCommonPrefix(null) = ""
     * StringToolkit.getCommonPrefix(new String[] {}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"abc"}) = "abc"
     * StringToolkit.getCommonPrefix(new String[] {null, null}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"", ""}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"", null}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"abc", null, null}) = ""
     * StringToolkit.getCommonPrefix(new String[] {null, null, "abc"}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"", "abc"}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"abc", ""}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"abc", "abc"}) = "abc"
     * StringToolkit.getCommonPrefix(new String[] {"abc", "a"}) = "a"
     * StringToolkit.getCommonPrefix(new String[] {"ab", "abxyz"}) = "ab"
     * StringToolkit.getCommonPrefix(new String[] {"abcde", "abxyz"}) = "ab"
     * StringToolkit.getCommonPrefix(new String[] {"abcde", "xyz"}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"xyz", "abcde"}) = ""
     * StringToolkit.getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) = "i am a "
     * </pre>
     *
     * @param strs  array of String objects, entries may be null
     * @return the initial sequence of characters that are common to all Strings
     * in the array; empty String if the array is null, the elements are all null
     * or if there is no common prefix.
     * @since 2.4
     */
    public static String getCommonPrefix(final String... strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY;
        }
        final int smallestIndexOfDiff = indexOfDifference(strs);
        if (smallestIndexOfDiff == INDEX_NOT_FOUND) {
            // all strings were identical
            if (strs[0] == null) {
                return EMPTY;
            }
            return strs[0];
        } else if (smallestIndexOfDiff == 0) {
            // there were no common initial characters
            return EMPTY;
        } else {
            // we found a common initial character sequence
            return strs[0].substring(0, smallestIndexOfDiff);
        }
    }

    // Misc
    //-----------------------------------------------------------------------
    /**
     * <p>Find the Levenshtein distance between two Strings.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * <p>The previous implementation of the Levenshtein distance algorithm
     * was from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError
     * which can occur when my Java implementation is used with very large strings.<br>
     * This implementation of the Levenshtein distance algorithm
     * is from <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a></p>
     *
     * <pre>
     * StringToolkit.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringToolkit.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringToolkit.getLevenshteinDistance("","")               = 0
     * StringToolkit.getLevenshteinDistance("","a")              = 1
     * StringToolkit.getLevenshteinDistance("aaapppp", "")       = 7
     * StringToolkit.getLevenshteinDistance("frog", "fog")       = 1
     * StringToolkit.getLevenshteinDistance("fly", "ant")        = 3
     * StringToolkit.getLevenshteinDistance("elephant", "hippo") = 7
     * StringToolkit.getLevenshteinDistance("hippo", "elephant") = 7
     * StringToolkit.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringToolkit.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s  the first String, must not be null
     * @param t  the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input {@code null}
     * @since 3.0 Changed signature from getLevenshteinDistance(String, String) to
     * getLevenshteinDistance(CharSequence, CharSequence)
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
           The difference between this impl. and the previous is that, rather
           than creating and retaining a matrix of size s.length() + 1 by t.length() + 1,
           we maintain two single-dimensional arrays of length s.length() + 1.  The first, d,
           is the 'current working' distance array that maintains the newest distance cost
           counts as we iterate through the characters of String s.  Each time we increment
           the index of String t we are comparing, d is copied to p, the second int[].  Doing so
           allows us to retain the previous cost counts as required by the algorithm (taking
           the minimum of the cost count to the left, up one, and diagonally up and to the left
           of the current cost count being calculated).  (Note that the arrays aren't really
           copied anymore, just switched...this is clearly much better than cloning an array
           or doing a System.arraycopy() each time  through the outer loop.)

           Effectively, the difference between the two implementations is this one does not
           cause an out of memory condition when calculating the LD over two very large strings.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * <p>Find the Levenshtein distance between two Strings if it's less than or equal to a given
     * threshold.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * <p>This implementation follows from Algorithms on Strings, Trees and Sequences by Dan Gusfield
     * and Chas Emerick's implementation of the Levenshtein distance algorithm from
     * <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <pre>
     * StringToolkit.getLevenshteinDistance(null, *, *)             = IllegalArgumentException
     * StringToolkit.getLevenshteinDistance(*, null, *)             = IllegalArgumentException
     * StringToolkit.getLevenshteinDistance(*, *, -1)               = IllegalArgumentException
     * StringToolkit.getLevenshteinDistance("","", 0)               = 0
     * StringToolkit.getLevenshteinDistance("aaapppp", "", 8)       = 7
     * StringToolkit.getLevenshteinDistance("aaapppp", "", 7)       = 7
     * StringToolkit.getLevenshteinDistance("aaapppp", "", 6))      = -1
     * StringToolkit.getLevenshteinDistance("elephant", "hippo", 7) = 7
     * StringToolkit.getLevenshteinDistance("elephant", "hippo", 6) = -1
     * StringToolkit.getLevenshteinDistance("hippo", "elephant", 7) = 7
     * StringToolkit.getLevenshteinDistance("hippo", "elephant", 6) = -1
     * </pre>
     *
     * @param s  the first String, must not be null
     * @param t  the second String, must not be null
     * @param threshold the target threshold, must not be negative
     * @return result distance, or {@code -1} if the distance would be greater than the threshold
     * @throws IllegalArgumentException if either String input {@code null} or negative threshold
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, final int threshold) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }

        /*
        This implementation only computes the distance if it's less than or equal to the
        threshold value, returning -1 if it's greater.  The advantage is performance: unbounded
        distance is O(nm), but a bound of k allows us to reduce it to O(km) time by only
        computing a diagonal stripe of width 2k + 1 of the cost table.
        It is also possible to use this to compute the unbounded Levenshtein distance by starting
        the threshold at 1 and doubling each time until the distance is found; this is O(dm), where
        d is the distance.

        One subtlety comes from needing to ignore entries on the border of our stripe
        eg.
        p[] = |#|#|#|*
        d[] =  *|#|#|#|
        We must ignore the entry to the left of the leftmost member
        We must ignore the entry above the rightmost member

        Another subtlety comes from our stripe running off the matrix if the strings aren't
        of the same size.  Since string s is always swapped to be the shorter of the two,
        the stripe will always run off to the upper right instead of the lower left of the matrix.

        As a concrete example, suppose s is of length 5, t is of length 7, and our threshold is 1.
        In this case we're going to walk a stripe of length 3.  The matrix would look like so:

           1 2 3 4 5
        1 |#|#| | | |
        2 |#|#|#| | |
        3 | |#|#|#| |
        4 | | |#|#|#|
        5 | | | |#|#|
        6 | | | | |#|
        7 | | | | | |

        Note how the stripe leads off the table as there is no possible way to turn a string of length 5
        into one of length 7 in edit distance of 1.

        Additionally, this implementation decreases memory usage by using two
        single-dimensional arrays and swapping them back and forth instead of allocating
        an entire n by m matrix.  This requires a few minor changes, such as immediately returning
        when it's detected that the stripe has run off the matrix and initially filling the arrays with
        large values so that entries we don't compute are ignored.

        See Algorithms on Strings, Trees and Sequences by Dan Gusfield for some discussion.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        // if one string is empty, the edit distance is necessarily the length of the other
        if (n == 0) {
            return m <= threshold ? m : -1;
        } else if (m == 0) {
            return n <= threshold ? n : -1;
        }

        if (n > m) {
            // swap the two strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // fill in starting table values
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }
        // these fills ensure that the value above the rightmost entry of our
        // stripe will be ignored in following loop iterations
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        // iterates through t
        for (int j = 1; j <= m; j++) {
            final char t_j = t.charAt(j - 1); // jth character of t
            d[0] = j;

            // compute stripe indices, constrain to array size
            final int min = Math.max(1, j - threshold);
            final int max = (j > Integer.MAX_VALUE - threshold) ? n : Math.min(n, j + threshold);

            // the stripe may lead off of the table if s and t are of different sizes
            if (min > max) {
                return -1;
            }

            // ignore entry left of leftmost
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            // iterates through [min, max] in s
            for (int i = min; i <= max; i++) {
                if (s.charAt(i - 1) == t_j) {
                    // diagonally left and up
                    d[i] = p[i - 1];
                } else {
                    // 1 + minimum of cell to the left, to the top, diagonally left and up
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                }
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // if p[n] is greater than the threshold, there's no guarantee on it being the correct
        // distance
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }

    /**
     * <p>Find the Jaro Winkler Distance which indicates the similarity score between two Strings.</p>
     *
     * <p>The Jaro measure is the weighted sum of percentage of matched characters from each file and transposed characters.
     * Winkler increased this measure for matching initial characters.</p>
     *
     * <p>This implementation is based on the Jaro Winkler similarity algorithm
     * from <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance</a>.</p>
     *
     * <pre>
     * StringToolkit.getJaroWinklerDistance(null, null)          = IllegalArgumentException
     * StringToolkit.getJaroWinklerDistance("","")               = 0.0
     * StringToolkit.getJaroWinklerDistance("","a")              = 0.0
     * StringToolkit.getJaroWinklerDistance("aaapppp", "")       = 0.0
     * StringToolkit.getJaroWinklerDistance("frog", "fog")       = 0.93
     * StringToolkit.getJaroWinklerDistance("fly", "ant")        = 0.0
     * StringToolkit.getJaroWinklerDistance("elephant", "hippo") = 0.44
     * StringToolkit.getJaroWinklerDistance("hippo", "elephant") = 0.44
     * StringToolkit.getJaroWinklerDistance("hippo", "zzzzzzzz") = 0.0
     * StringToolkit.getJaroWinklerDistance("hello", "hallo")    = 0.88
     * StringToolkit.getJaroWinklerDistance("ABC Corporation", "ABC Corp") = 0.91
     * StringToolkit.getJaroWinklerDistance("D N H Enterprises Inc", "D &amp; H Enterprises, Inc.") = 0.93
     * StringToolkit.getJaroWinklerDistance("My Gym Children's Fitness Center", "My Gym. Childrens Fitness") = 0.94
     * StringToolkit.getJaroWinklerDistance("PENNSYLVANIA", "PENNCISYLVNIA")    = 0.9
     * </pre>
     *
     * @param first the first String, must not be null
     * @param second the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input {@code null}
     * @since 3.3
     */
    public static double getJaroWinklerDistance(final CharSequence first, final CharSequence second) {
        final double DEFAULT_SCALING_FACTOR = 0.1;

        if (first == null || second == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        final double jaro = score(first,second);
        final int cl = commonPrefixLength(first, second);
        final double matchScore = Math.round((jaro + (DEFAULT_SCALING_FACTOR * cl * (1.0 - jaro))) *100.0)/100.0;

        return  matchScore;
    }

    /**
     * This method returns the Jaro-Winkler score for string matching.
     * @param first the first string to be matched
     * @param second the second string to be machted
     * @return matching score without scaling factor impact
     */
    private static double score(final CharSequence first, final CharSequence second) {
        String shorter;
        String longer;

        // Determine which String is longer.
        if (first.length() > second.length()) {
            longer = first.toString().toLowerCase();
            shorter = second.toString().toLowerCase();
        } else {
            longer = second.toString().toLowerCase();
            shorter = first.toString().toLowerCase();
        }

        // Calculate the half length() distance of the shorter String.
        final int halflength = (shorter.length() / 2) + 1;

        // Find the set of matching characters between the shorter and longer strings. Note that
        // the set of matching characters may be different depending on the order of the strings.
        final String m1 = getSetOfMatchingCharacterWithin(shorter, longer, halflength);
        final String m2 = getSetOfMatchingCharacterWithin(longer, shorter, halflength);

        // If one or both of the sets of common characters is empty, then
        // there is no similarity between the two strings.
        if (m1.length() == 0 || m2.length() == 0) {
            return 0.0;
        }

        // If the set of common characters is not the same size, then
        // there is no similarity between the two strings, either.
        if (m1.length() != m2.length()) {
            return 0.0;
        }

        // Calculate the number of transposition between the two sets
        // of common characters.
        final int transpositions = transpositions(m1, m2);

        // Calculate the distance.
        final double dist =
                (m1.length() / ((double)shorter.length()) +
                        m2.length() / ((double)longer.length()) +
                        (m1.length() - transpositions) / ((double)m1.length())) / 3.0;
        return dist;
    }

    /**
     * <p>Find the Fuzzy Distance which indicates the similarity score between two Strings.</p>
     *
     * <p>This string matching algorithm is similar to the algorithms of editors such as Sublime Text,
     * TextMate, Atom and others. One point is given for every matched character. Subsequent
     * matches yield two bonus points. A higher score indicates a higher similarity.</p>
     *
     * <pre>
     * StringToolkit.getFuzzyDistance(null, null, null)                                    = IllegalArgumentException
     * StringToolkit.getFuzzyDistance("", "", Locale.ENGLISH)                              = 0
     * StringToolkit.getFuzzyDistance("Workshop", "b", Locale.ENGLISH)                     = 0
     * StringToolkit.getFuzzyDistance("Room", "o", Locale.ENGLISH)                         = 1
     * StringToolkit.getFuzzyDistance("Workshop", "w", Locale.ENGLISH)                     = 1
     * StringToolkit.getFuzzyDistance("Workshop", "ws", Locale.ENGLISH)                    = 2
     * StringToolkit.getFuzzyDistance("Workshop", "wo", Locale.ENGLISH)                    = 4
     * StringToolkit.getFuzzyDistance("Apache Software Foundation", "asf", Locale.ENGLISH) = 3
     * </pre>
     *
     * @param term a full term that should be matched against, must not be null
     * @param query the query that will be matched against a term, must not be null
     * @param locale This string matching logic is case insensitive. A locale is necessary to normalize
     *  both Strings to lower case.
     * @return result score
     * @throws IllegalArgumentException if either String input {@code null} or Locale input {@code null}
     * @since 3.4
     */
    public static int getFuzzyDistance(final CharSequence term, final CharSequence query, final Locale locale) {
        if (term == null || query == null) {
            throw new IllegalArgumentException("Strings must not be null");
        } else if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null");
        }

        // fuzzy logic is case insensitive. We normalize the Strings to lower
        // case right from the start. Turning characters to lower case
        // via Character.toLowerCase(char) is unfortunately insufficient
        // as it does not accept a locale.
        final String termLowerCase = term.toString().toLowerCase(locale);
        final String queryLowerCase = query.toString().toLowerCase(locale);

        // the resulting score
        int score = 0;

        // the position in the term which will be scanned next for potential
        // query character matches
        int termIndex = 0;

        // index of the previously matched character in the term
        int previousMatchingCharacterIndex = Integer.MIN_VALUE;

        for (int queryIndex = 0; queryIndex < queryLowerCase.length(); queryIndex++) {
            final char queryChar = queryLowerCase.charAt(queryIndex);

            boolean termCharacterMatchFound = false;
            for (; termIndex < termLowerCase.length() && !termCharacterMatchFound; termIndex++) {
                final char termChar = termLowerCase.charAt(termIndex);

                if (queryChar == termChar) {
                    // simple character matches result in one point
                    score++;

                    // subsequent character matches further improve
                    // the score.
                    if (previousMatchingCharacterIndex + 1 == termIndex) {
                        score += 2;
                    }

                    previousMatchingCharacterIndex = termIndex;

                    // we can leave the nested loop. Every character in the
                    // query can match at most one character in the term.
                    termCharacterMatchFound = true;
                }
            }
        }

        return score;
    }

    /**
     * Gets a set of matching characters between two strings.
     *
     * <p><Two characters from the first string and the second string are considered matching if the character's
     * respective positions are no farther than the limit value.</p>
     *
     * @param first The first string.
     * @param second The second string.
     * @param limit The maximum distance to consider.
     * @return A string contain the set of common characters.
     */
    private static String getSetOfMatchingCharacterWithin(final CharSequence first, final CharSequence second, final int limit) {
        final StringBuilder common = new StringBuilder();
        final StringBuilder copy = new StringBuilder(second);

        for (int i = 0; i < first.length(); i++) {
            final char ch = first.charAt(i);
            boolean found = false;

            // See if the character is within the limit positions away from the original position of that character.
            for (int j = Math.max(0, i - limit); !found && j < Math.min(i + limit, second.length()); j++) {
                if (copy.charAt(j) == ch) {
                    found = true;
                    common.append(ch);
                    copy.setCharAt(j,'*');
                }
            }
        }
        return common.toString();
    }

    /**
     * Calculates the number of transposition between two strings.
     * @param first The first string.
     * @param second The second string.
     * @return The number of transposition between the two strings.
     */
    private static int transpositions(final CharSequence first, final CharSequence second) {
        int transpositions = 0;
        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                transpositions++;
            }
        }
        return transpositions / 2;
    }

    /**
     * Calculates the number of characters from the beginning of the strings that match exactly one-to-one,
     * up to a maximum of four (4) characters.
     * @param first The first string.
     * @param second The second string.
     * @return A number between 0 and 4.
     */
    private static int commonPrefixLength(final CharSequence first, final CharSequence second) {
        final int result = getCommonPrefix(first.toString(), second.toString()).length();

        // Limit the result to 4.
        return result > 4 ? 4 : result;
    }

    // startsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a CharSequence starts with a specified prefix.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringToolkit.startsWith(null, null)      = true
     * StringToolkit.startsWith(null, "abc")     = false
     * StringToolkit.startsWith("abcdef", null)  = false
     * StringToolkit.startsWith("abcdef", "abc") = true
     * StringToolkit.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @see String#startsWith(String)
     * @param str  the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @return {@code true} if the CharSequence starts with the prefix, case sensitive, or
     *  both {@code null}
     * @since 2.4
     * @since 3.0 Changed signature from startsWith(String, String) to startsWith(CharSequence, CharSequence)
     */
    public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * <p>Case insensitive check if a CharSequence starts with a specified prefix.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * StringToolkit.startsWithIgnoreCase(null, null)      = true
     * StringToolkit.startsWithIgnoreCase(null, "abc")     = false
     * StringToolkit.startsWithIgnoreCase("abcdef", null)  = false
     * StringToolkit.startsWithIgnoreCase("abcdef", "abc") = true
     * StringToolkit.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     *
     * @see String#startsWith(String)
     * @param str  the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @return {@code true} if the CharSequence starts with the prefix, case insensitive, or
     *  both {@code null}
     * @since 2.4
     * @since 3.0 Changed signature from startsWithIgnoreCase(String, String) to startsWithIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * <p>Check if a CharSequence starts with a specified prefix (optionally case insensitive).</p>
     *
     * @see String#startsWith(String)
     * @param str  the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return {@code true} if the CharSequence starts with the prefix or
     *  both {@code null}
     */
    private static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return CharSequenceToolkit.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * <p>Check if a CharSequence starts with any of an array of specified strings.</p>
     *
     * <pre>
     * StringToolkit.startsWithAny(null, null)      = false
     * StringToolkit.startsWithAny(null, new String[] {"abc"})  = false
     * StringToolkit.startsWithAny("abcxyz", null)     = false
     * StringToolkit.startsWithAny("abcxyz", new String[] {""}) = false
     * StringToolkit.startsWithAny("abcxyz", new String[] {"abc"}) = true
     * StringToolkit.startsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @param string  the CharSequence to check, may be null
     * @param searchStrings the CharSequences to find, may be null or empty
     * @return {@code true} if the CharSequence starts with any of the the prefixes, case insensitive, or
     *  both {@code null}
     * @since 2.5
     * @since 3.0 Changed signature from startsWithAny(String, String[]) to startsWithAny(CharSequence, CharSequence...)
     */
    public static boolean startsWithAny(final CharSequence string, final CharSequence... searchStrings) {
        if (isEmpty(string) || ObjectToolkit.isEmpty(searchStrings)) {
            return false;
        }
        for (final CharSequence searchString : searchStrings) {
            if (startsWith(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    // endsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a CharSequence ends with a specified suffix.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringToolkit.endsWith(null, null)      = true
     * StringToolkit.endsWith(null, "def")     = false
     * StringToolkit.endsWith("abcdef", null)  = false
     * StringToolkit.endsWith("abcdef", "def") = true
     * StringToolkit.endsWith("ABCDEF", "def") = false
     * StringToolkit.endsWith("ABCDEF", "cde") = false
     * </pre>
     *
     * @see String#endsWith(String)
     * @param str  the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case sensitive, or
     *  both {@code null}
     * @since 2.4
     * @since 3.0 Changed signature from endsWith(String, String) to endsWith(CharSequence, CharSequence)
     */
    public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * <p>Case insensitive check if a CharSequence ends with a specified suffix.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * StringToolkit.endsWithIgnoreCase(null, null)      = true
     * StringToolkit.endsWithIgnoreCase(null, "def")     = false
     * StringToolkit.endsWithIgnoreCase("abcdef", null)  = false
     * StringToolkit.endsWithIgnoreCase("abcdef", "def") = true
     * StringToolkit.endsWithIgnoreCase("ABCDEF", "def") = true
     * StringToolkit.endsWithIgnoreCase("ABCDEF", "cde") = false
     * </pre>
     *
     * @see String#endsWith(String)
     * @param str  the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case insensitive, or
     *  both {@code null}
     * @since 2.4
     * @since 3.0 Changed signature from endsWithIgnoreCase(String, String) to endsWithIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>Check if a CharSequence ends with a specified suffix (optionally case insensitive).</p>
     *
     * @see String#endsWith(String)
     * @param str  the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return {@code true} if the CharSequence starts with the prefix or
     *  both {@code null}
     */
    private static boolean endsWith(final CharSequence str, final CharSequence suffix, final boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == null && suffix == null;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        final int strOffset = str.length() - suffix.length();
        return CharSequenceToolkit.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    /**
     * <p>
     * Similar to <a
     * href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize
     * -space</a>
     * </p>
     * <p>
     * The function returns the argument string with whitespace normalized by using
     * <code>{@link #trim(String)}</code> to remove leading and trailing whitespace
     * and then replacing sequences of whitespace characters by a single space.
     * </p>
     * In XML Whitespace characters are the same as those allowed by the <a
     * href="http://www.w3.org/TR/REC-xml/#NT-S">S</a> production, which is S ::= (#x20 | #x9 | #xD | #xA)+
     * <p>
     * Java's regexp pattern \s defines whitespace as [ \t\n\x0B\f\r]
     *
     * <p>For reference:</p>
     * <ul>
     * <li>\x0B = vertical tab</li>
     * <li>\f = #xC = form feed</li>
     * <li>#x20 = space</li>
     * <li>#x9 = \t</li>
     * <li>#xA = \n</li>
     * <li>#xD = \r</li>
     * </ul>
     *
     * <p>
     * The difference is that Java's whitespace includes vertical tab and form feed, which this functional will also
     * normalize. Additionally <code>{@link #trim(String)}</code> removes control characters (char &lt;= 32) from both
     * ends of this String.
     * </p>
     *
     * @see Pattern
     * @see #trim(String)
     * @see <a
     *      href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize-space</a>
     * @param str the source String to normalize whitespaces from, may be null
     * @return the modified string with whitespace normalized, {@code null} if null String input
     *
     * @since 3.0
     */
    public static String normalizeSpace(final String str) {
        // LANG-1020: Improved performance significantly by normalizing manually instead of using regex
        // See https://github.com/librucha/commons-lang-normalizespaces-benchmark for performance test
        if (isEmpty(str)) {
            return str;
        }
        final int size = str.length();
        final char[] newChars = new char[size];
        int count = 0;
        int whitespacesCount = 0;
        boolean startWhitespaces = true;
        for (int i = 0; i < size; i++) {
            char actualChar = str.charAt(i);
            boolean isWhitespace = Character.isWhitespace(actualChar);
            if (!isWhitespace) {
                startWhitespaces = false;
                newChars[count++] = (actualChar == 160 ? 32 : actualChar);
                whitespacesCount = 0;
            } else {
                if (whitespacesCount == 0 && !startWhitespaces) {
                    newChars[count++] = SPACE.charAt(0);
                }
                whitespacesCount++;
            }
        }
        if (startWhitespaces) {
            return EMPTY;
        }
        return new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0));
    }

    /**
     * <p>Check if a CharSequence ends with any of an array of specified strings.</p>
     *
     * <pre>
     * StringToolkit.endsWithAny(null, null)      = false
     * StringToolkit.endsWithAny(null, new String[] {"abc"})  = false
     * StringToolkit.endsWithAny("abcxyz", null)     = false
     * StringToolkit.endsWithAny("abcxyz", new String[] {""}) = true
     * StringToolkit.endsWithAny("abcxyz", new String[] {"xyz"}) = true
     * StringToolkit.endsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @param string  the CharSequence to check, may be null
     * @param searchStrings the CharSequences to find, may be null or empty
     * @return {@code true} if the CharSequence ends with any of the the prefixes, case insensitive, or
     *  both {@code null}
     * @since 3.0
     */
    public static boolean endsWithAny(final CharSequence string, final CharSequence... searchStrings) {
        if (isEmpty(string) || ObjectToolkit.isEmpty(searchStrings)) {
            return false;
        }
        for (final CharSequence searchString : searchStrings) {
            if (endsWith(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends the suffix to the end of the string if the string does not
     * already end in the suffix.
     *
     * @param str The string.
     * @param suffix The suffix to append to the end of the string.
     * @param ignoreCase Indicates whether the compare should ignore case.
     * @param suffixes Additional suffixes that are valid terminators (optional).
     *
     * @return A new String if suffix was appened, the same string otherwise.
     */
    private static String appendIfMissing(final String str, final CharSequence suffix, final boolean ignoreCase, final CharSequence... suffixes) {
        if (str == null || isEmpty(suffix) || endsWith(str, suffix, ignoreCase)) {
            return str;
        }
        if (suffixes != null && suffixes.length > 0) {
            for (final CharSequence s : suffixes) {
                if (endsWith(str, s, ignoreCase)) {
                    return str;
                }
            }
        }
        return str + suffix.toString();
    }

    /**
     * Appends the suffix to the end of the string if the string does not
     * already end with any the suffixes.
     *
     * <pre>
     * StringToolkit.appendIfMissing(null, null) = null
     * StringToolkit.appendIfMissing("abc", null) = "abc"
     * StringToolkit.appendIfMissing("", "xyz") = "xyz"
     * StringToolkit.appendIfMissing("abc", "xyz") = "abcxyz"
     * StringToolkit.appendIfMissing("abcxyz", "xyz") = "abcxyz"
     * StringToolkit.appendIfMissing("abcXYZ", "xyz") = "abcXYZxyz"
     * </pre>
     * <p>With additional suffixes,</p>
     * <pre>
     * StringToolkit.appendIfMissing(null, null, null) = null
     * StringToolkit.appendIfMissing("abc", null, null) = "abc"
     * StringToolkit.appendIfMissing("", "xyz", null) = "xyz"
     * StringToolkit.appendIfMissing("abc", "xyz", new CharSequence[]{null}) = "abcxyz"
     * StringToolkit.appendIfMissing("abc", "xyz", "") = "abc"
     * StringToolkit.appendIfMissing("abc", "xyz", "mno") = "abcxyz"
     * StringToolkit.appendIfMissing("abcxyz", "xyz", "mno") = "abcxyz"
     * StringToolkit.appendIfMissing("abcmno", "xyz", "mno") = "abcmno"
     * StringToolkit.appendIfMissing("abcXYZ", "xyz", "mno") = "abcXYZxyz"
     * StringToolkit.appendIfMissing("abcMNO", "xyz", "mno") = "abcMNOxyz"
     * </pre>
     *
     * @param str The string.
     * @param suffix The suffix to append to the end of the string.
     * @param suffixes Additional suffixes that are valid terminators.
     *
     * @return A new String if suffix was appened, the same string otherwise.
     *
     * @since 3.2
     */
    public static String appendIfMissing(final String str, final CharSequence suffix, final CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    /**
     * Appends the suffix to the end of the string if the string does not
     * already end, case insensitive, with any of the suffixes.
     *
     * <pre>
     * StringToolkit.appendIfMissingIgnoreCase(null, null) = null
     * StringToolkit.appendIfMissingIgnoreCase("abc", null) = "abc"
     * StringToolkit.appendIfMissingIgnoreCase("", "xyz") = "xyz"
     * StringToolkit.appendIfMissingIgnoreCase("abc", "xyz") = "abcxyz"
     * StringToolkit.appendIfMissingIgnoreCase("abcxyz", "xyz") = "abcxyz"
     * StringToolkit.appendIfMissingIgnoreCase("abcXYZ", "xyz") = "abcXYZ"
     * </pre>
     * <p>With additional suffixes,</p>
     * <pre>
     * StringToolkit.appendIfMissingIgnoreCase(null, null, null) = null
     * StringToolkit.appendIfMissingIgnoreCase("abc", null, null) = "abc"
     * StringToolkit.appendIfMissingIgnoreCase("", "xyz", null) = "xyz"
     * StringToolkit.appendIfMissingIgnoreCase("abc", "xyz", new CharSequence[]{null}) = "abcxyz"
     * StringToolkit.appendIfMissingIgnoreCase("abc", "xyz", "") = "abc"
     * StringToolkit.appendIfMissingIgnoreCase("abc", "xyz", "mno") = "axyz"
     * StringToolkit.appendIfMissingIgnoreCase("abcxyz", "xyz", "mno") = "abcxyz"
     * StringToolkit.appendIfMissingIgnoreCase("abcmno", "xyz", "mno") = "abcmno"
     * StringToolkit.appendIfMissingIgnoreCase("abcXYZ", "xyz", "mno") = "abcXYZ"
     * StringToolkit.appendIfMissingIgnoreCase("abcMNO", "xyz", "mno") = "abcMNO"
     * </pre>
     *
     * @param str The string.
     * @param suffix The suffix to append to the end of the string.
     * @param suffixes Additional suffixes that are valid terminators.
     *
     * @return A new String if suffix was appened, the same string otherwise.
     *
     * @since 3.2
     */
    public static String appendIfMissingIgnoreCase(final String str, final CharSequence suffix, final CharSequence... suffixes) {
        return appendIfMissing(str, suffix, true, suffixes);
    }

    /**
     * Prepends the prefix to the start of the string if the string does not
     * already start with any of the prefixes.
     *
     * @param str The string.
     * @param prefix The prefix to prepend to the start of the string.
     * @param ignoreCase Indicates whether the compare should ignore case.
     * @param prefixes Additional prefixes that are valid (optional).
     *
     * @return A new String if prefix was prepended, the same string otherwise.
     */
    private static String prependIfMissing(final String str, final CharSequence prefix, final boolean ignoreCase, final CharSequence... prefixes) {
        if (str == null || isEmpty(prefix) || startsWith(str, prefix, ignoreCase)) {
            return str;
        }
        if (prefixes != null && prefixes.length > 0) {
            for (final CharSequence p : prefixes) {
                if (startsWith(str, p, ignoreCase)) {
                    return str;
                }
            }
        }
        return prefix.toString() + str;
    }

    /**
     * Prepends the prefix to the start of the string if the string does not
     * already start with any of the prefixes.
     *
     * <pre>
     * StringToolkit.prependIfMissing(null, null) = null
     * StringToolkit.prependIfMissing("abc", null) = "abc"
     * StringToolkit.prependIfMissing("", "xyz") = "xyz"
     * StringToolkit.prependIfMissing("abc", "xyz") = "xyzabc"
     * StringToolkit.prependIfMissing("xyzabc", "xyz") = "xyzabc"
     * StringToolkit.prependIfMissing("XYZabc", "xyz") = "xyzXYZabc"
     * </pre>
     * <p>With additional prefixes,</p>
     * <pre>
     * StringToolkit.prependIfMissing(null, null, null) = null
     * StringToolkit.prependIfMissing("abc", null, null) = "abc"
     * StringToolkit.prependIfMissing("", "xyz", null) = "xyz"
     * StringToolkit.prependIfMissing("abc", "xyz", new CharSequence[]{null}) = "xyzabc"
     * StringToolkit.prependIfMissing("abc", "xyz", "") = "abc"
     * StringToolkit.prependIfMissing("abc", "xyz", "mno") = "xyzabc"
     * StringToolkit.prependIfMissing("xyzabc", "xyz", "mno") = "xyzabc"
     * StringToolkit.prependIfMissing("mnoabc", "xyz", "mno") = "mnoabc"
     * StringToolkit.prependIfMissing("XYZabc", "xyz", "mno") = "xyzXYZabc"
     * StringToolkit.prependIfMissing("MNOabc", "xyz", "mno") = "xyzMNOabc"
     * </pre>
     *
     * @param str The string.
     * @param prefix The prefix to prepend to the start of the string.
     * @param prefixes Additional prefixes that are valid.
     *
     * @return A new String if prefix was prepended, the same string otherwise.
     *
     * @since 3.2
     */
    public static String prependIfMissing(final String str, final CharSequence prefix, final CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    /**
     * Prepends the prefix to the start of the string if the string does not
     * already start, case insensitive, with any of the prefixes.
     *
     * <pre>
     * StringToolkit.prependIfMissingIgnoreCase(null, null) = null
     * StringToolkit.prependIfMissingIgnoreCase("abc", null) = "abc"
     * StringToolkit.prependIfMissingIgnoreCase("", "xyz") = "xyz"
     * StringToolkit.prependIfMissingIgnoreCase("abc", "xyz") = "xyzabc"
     * StringToolkit.prependIfMissingIgnoreCase("xyzabc", "xyz") = "xyzabc"
     * StringToolkit.prependIfMissingIgnoreCase("XYZabc", "xyz") = "XYZabc"
     * </pre>
     * <p>With additional prefixes,</p>
     * <pre>
     * StringToolkit.prependIfMissingIgnoreCase(null, null, null) = null
     * StringToolkit.prependIfMissingIgnoreCase("abc", null, null) = "abc"
     * StringToolkit.prependIfMissingIgnoreCase("", "xyz", null) = "xyz"
     * StringToolkit.prependIfMissingIgnoreCase("abc", "xyz", new CharSequence[]{null}) = "xyzabc"
     * StringToolkit.prependIfMissingIgnoreCase("abc", "xyz", "") = "abc"
     * StringToolkit.prependIfMissingIgnoreCase("abc", "xyz", "mno") = "xyzabc"
     * StringToolkit.prependIfMissingIgnoreCase("xyzabc", "xyz", "mno") = "xyzabc"
     * StringToolkit.prependIfMissingIgnoreCase("mnoabc", "xyz", "mno") = "mnoabc"
     * StringToolkit.prependIfMissingIgnoreCase("XYZabc", "xyz", "mno") = "XYZabc"
     * StringToolkit.prependIfMissingIgnoreCase("MNOabc", "xyz", "mno") = "MNOabc"
     * </pre>
     *
     * @param str The string.
     * @param prefix The prefix to prepend to the start of the string.
     * @param prefixes Additional prefixes that are valid (optional).
     *
     * @return A new String if prefix was prepended, the same string otherwise.
     *
     * @since 3.2
     */
    public static String prependIfMissingIgnoreCase(final String str, final CharSequence prefix, final CharSequence... prefixes) {
        return prependIfMissing(str, prefix, true, prefixes);
    }

    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes
     *            the byte array to read from
     * @param charsetName
     *            the encoding to use, if null then use the platform default
     * @return a new String
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     * @throws NullPointerException
     *             if the input is null
     * @since 3.1
     */
    @Deprecated
    public static String toString(final byte[] bytes, final String charsetName) throws UnsupportedEncodingException {
        return charsetName != null ? new String(bytes, charsetName) : new String(bytes, Charset.defaultCharset());
    }

    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes
     *            the byte array to read from
     * @param charset
     *            the encoding to use, if null then use the platform default
     * @return a new String
     * @throws NullPointerException
     *             if {@code bytes} is null
     * @since 3.2
     * @since 3.3 No longer throws {@link UnsupportedEncodingException}.
     */
    public static String toEncodedString(final byte[] bytes, final Charset charset) {
        return new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }

    /**
     * <p>
     * Wraps a string with a char.
     * </p>
     *
     * <pre>
     * StringToolkit.wrap(null, *)        = null
     * StringToolkit.wrap("", *)          = ""
     * StringToolkit.wrap("ab", '\0')     = "ab"
     * StringToolkit.wrap("ab", 'x')      = "xabx"
     * StringToolkit.wrap("ab", '\'')     = "'ab'"
     * StringToolkit.wrap("\"ab\"", '\"') = "\"\"ab\"\""
     * </pre>
     *
     * @param str
     *            the string to be wrapped, may be {@code null}
     * @param wrapWith
     *            the char that will wrap {@code str}
     * @return the wrapped string, or {@code null} if {@code str==null}
     * @since 3.4
     */
    public static String wrap(final String str, final char wrapWith) {

        if (isEmpty(str) || wrapWith == '\0') {
            return str;
        }

        return wrapWith + str + wrapWith;
    }

    /**
     * <p>
     * Wraps a String with another String.
     * </p>
     *
     * <p>
     * A {@code null} input String returns {@code null}.
     * </p>
     *
     * <pre>
     * StringToolkit.wrap(null, *)         = null
     * StringToolkit.wrap("", *)           = ""
     * StringToolkit.wrap("ab", null)      = "ab"
     * StringToolkit.wrap("ab", "x")       = "xabx"
     * StringToolkit.wrap("ab", "\"")      = "\"ab\""
     * StringToolkit.wrap("\"ab\"", "\"")  = "\"\"ab\"\""
     * StringToolkit.wrap("ab", "'")       = "'ab'"
     * StringToolkit.wrap("'abcd'", "'")   = "''abcd''"
     * StringToolkit.wrap("\"abcd\"", "'") = "'\"abcd\"'"
     * StringToolkit.wrap("'abcd'", "\"")  = "\"'abcd'\""
     * </pre>
     *
     * @param str
     *            the String to be wrapper, may be null
     * @param wrapWith
     *            the String that will wrap str
     * @return wrapped String, {@code null} if null String input
     * @since 3.4
     */
    public static String wrap(final String str, final String wrapWith) {

        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }

        return wrapWith.concat(str).concat(wrapWith);
    }

}
