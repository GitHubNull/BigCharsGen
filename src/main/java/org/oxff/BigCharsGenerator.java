package org.oxff;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

public class BigCharsGenerator {
    private final static int ONE_KB = 1024;
    private final static int ONE_MB = ONE_KB * 1024;
    private final static HashMap<String, Integer> SIZE_STRING_INTEGER_MAP = new HashMap<>();

    public BigCharsGenerator() {
        /*
            "128KB",
            "256KB",
            "512KkB",
            "1MB"
         */
        SIZE_STRING_INTEGER_MAP.put("128KB", ONE_KB * 128);
        SIZE_STRING_INTEGER_MAP.put("256KB", ONE_KB * 256);
        SIZE_STRING_INTEGER_MAP.put("51kKB", ONE_KB * 512);
        SIZE_STRING_INTEGER_MAP.put("1MB", ONE_KB * 1024);
    }

    // 判断用户输入的用于描述要生成填充字符串的大小字符串是否合法
    public boolean isValidCustomCharacterCountKey(String characterCountKey) {
        // 判断是否为空
        if (characterCountKey == null || characterCountKey.isEmpty()) {
            return false;
        }

        // 是否是纯数字且是正整数
        return characterCountKey.matches("^\\d+B?$") || characterCountKey.matches("^\\d+[kKmM]B?$");

    }

    /**
     * 计算用户输入的用于描述要生成填充字符串的大小字符串对应的字节数
     *
     * @param characterCountKey 用于描述要生成填充字符串的大小字符串(1, 1B, 1k, 1K, 1KB, 1kB, 1m, 1M, 1mB, 1MB 都是合法的)
     * @return 返回字节数
     * @throws IllegalArgumentException 如果输入的字符串格式不正确
     */
    public int getCharacterCount(String characterCountKey) {
        if (characterCountKey == null || characterCountKey.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: characterCountKey is null or empty.");
        }

        // 提取数字部分
        String numberPart = characterCountKey.replaceAll("[kKmMB]", "");
        int number;
        try {
            number = Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse number from characterCountKey: " + characterCountKey, e);
        }

        // 根据单位计算结果
        String unitPart = characterCountKey.replaceAll("\\d+", "").toUpperCase();
        return switch (unitPart) {
            case "", "B" -> number;
            case "K", "KB" -> number * ONE_KB;
            case "M", "MB" -> number * ONE_MB;
            default ->
                    throw new IllegalArgumentException("Unsupported unit in characterCountKey: " + characterCountKey + "unitPart: " + unitPart);
        };
    }


    private void generator(MontoyaApi api, ContextMenuEvent contextMenuEvent, int characterCount) {
        api.logging().logToOutput("generator, characterCount: " + characterCount);
        if (contextMenuEvent.messageEditorRequestResponse().isEmpty()) {
            api.logging().logToError("messageEditorRequestResponse is empty");
            return;
        }
        try {
            StringBuilder sb = appendChars(characterCount);
            Optional<Range> range = contextMenuEvent.messageEditorRequestResponse().flatMap(MessageEditorHttpRequestResponse::selectionOffsets);
            if (range.isPresent()) {
                int left = range.get().startIndexInclusive();
                int right = range.get().endIndexExclusive();
                byte[] allBytes = contextMenuEvent.messageEditorRequestResponse().get().requestResponse().request().toByteArray().getBytes();
                byte[] leftBytes = new byte[left];
                System.arraycopy(allBytes, 0, leftBytes, 0, left);
                byte[] rightBytes = new byte[allBytes.length - right];
                System.arraycopy(allBytes, right, rightBytes, 0, allBytes.length - right);
                byte[] finalBytes = new byte[leftBytes.length + sb.length() + rightBytes.length];
                System.arraycopy(leftBytes, 0, finalBytes, 0, leftBytes.length);
                System.arraycopy(sb.toString().getBytes(), 0, finalBytes, leftBytes.length, sb.length());
                System.arraycopy(rightBytes, 0, finalBytes, leftBytes.length + sb.length(), rightBytes.length);
                contextMenuEvent.messageEditorRequestResponse().get().setRequest(HttpRequest.httpRequest(ByteArray.byteArray(finalBytes)));
            } else {
                // insert
                api.logging().logToOutput("Insert Big Chars, range: " + range);
                int left = contextMenuEvent.messageEditorRequestResponse().get().caretPosition();
                byte[] allBytes = contextMenuEvent.messageEditorRequestResponse().get().requestResponse().request().toByteArray().getBytes();
                byte[] leftBytes = new byte[left];
                System.arraycopy(allBytes, 0, leftBytes, 0, left);
                byte[] rightBytes = new byte[allBytes.length - left];
                System.arraycopy(allBytes, left, rightBytes, 0, allBytes.length - left);
                byte[] finalBytes = new byte[leftBytes.length + sb.length() + rightBytes.length];
                System.arraycopy(leftBytes, 0, finalBytes, 0, leftBytes.length);
                System.arraycopy(sb.toString().getBytes(), 0, finalBytes, leftBytes.length, sb.length());
                System.arraycopy(rightBytes, 0, finalBytes, leftBytes.length + sb.length(), rightBytes.length);
                contextMenuEvent.messageEditorRequestResponse().get().setRequest(HttpRequest.httpRequest(ByteArray.byteArray(finalBytes)));
            }


        } catch (NullPointerException | NoSuchElementException | IndexOutOfBoundsException e) {
            // 这里可以记录错误日志或者进行其他错误处理
            api.logging().logToError("An error occurred: " + e.getMessage());
        }
    }

    public void generateBySelected(MontoyaApi api, ContextMenuEvent contextMenuEvent, String characterCountKey) {
        api.logging().logToOutput("Generate Big Chars, characterCountKey: " + characterCountKey);
        try {
            Integer characterCount = SIZE_STRING_INTEGER_MAP.get(characterCountKey);
            if (characterCount == null) {
                api.logging().logToError("Invalid character count key: " + characterCountKey);
                return;
            }

            generator(api, contextMenuEvent, characterCount);
        } catch (Exception e) {
            api.logging().logToError("An error occurred: " + e.getMessage());
        }
    }

    private StringBuilder appendChars(int count) {
        StringBuilder sb = new StringBuilder(count); // 预设长度
        sb.setLength(count); // 设置长度
        for (int i = 0; i < count; i++) {
            sb.setCharAt(i, 'Q'); // 设置每个位置的字符
        }
        return sb;
    }

    public void generateByCustom(MontoyaApi api, ContextMenuEvent contextMenuEvent, String characterCountKey) {
        api.logging().logToOutput("Generate Big Chars, text: " + characterCountKey);
        if (!isValidCustomCharacterCountKey(characterCountKey)) {
            api.logging().logToError("Invalid character count key: " + characterCountKey);
            return;
        }

        int characterCount = getCharacterCount(characterCountKey);
        if (characterCount == 0) {
            api.logging().logToError("characterCount is 0");
            return;
        }
        generator(api, contextMenuEvent, characterCount);
    }

}
