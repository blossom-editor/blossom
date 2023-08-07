import {
  marked,
  MarkedExtension,
  MarkedOptions,
  TokensList,
  Token,
} from "marked";

/** marked 拓展 Marked 类, 用于创建新的实例 */
declare module "marked" {
  export class Marked {
    /**
     * Compiles markdown to HTML.
     *
     * @param src String of markdown source to be compiled
     * @param callback Function called when the markdownString has been fully parsed when using async highlighting
     * @return String of compiled HTML
     */
    parse(src: string, callback: (error: any, parseResult: string) => void): void;

    /**
     * Compiles markdown to HTML asynchronously.
     *
     * @param src String of markdown source to be compiled
     * @param options Hash of options having async: true
     * @return Promise of string of compiled HTML
     */
    parse(src: string, options: MarkedOptions & { async: true }): Promise<string>;

    /**
     * Compiles markdown to HTML synchronously.
     *
     * @param src String of markdown source to be compiled
     * @param options Optional hash of options
     * @return String of compiled HTML
     */
    parse(src: string, options?: MarkedOptions): string;

    /**
     * Compiles markdown to HTML synchronously.
     *
     * @param src String of markdown source to be compiled
     * @param options Optional hash of options
     * @param callback Function called when the markdownString has been fully parsed when using async highlighting
     * @return String of compiled HTML
     */
    parse(src: string, options: MarkedOptions, callback: (error: any, parseResult: string) => void): void;

    /**
     * Compiles markdown to HTML without enclosing `p` tag.
     *
     * @param src String of markdown source to be compiled
     * @param options Hash of options
     * @return String of compiled HTML
     */
    parseInline(src: string, options?: MarkedOptions): string;

    /**
     * Gets the original marked default options.
     */
    getDefaults(): MarkedOptions;

    walkTokens(tokens: Token[] | TokensList, callback: (token: Token) => void): Marked;

    /**
     * Sets the default options.
     *
     * @param options Hash of options
     */
    setOptions(options: MarkedOptions): Marked;

    /**
     * Use Extension
     * @param MarkedExtension
     */
    use(...extensions: MarkedExtension[]): void;

    walkTokens(tokens: Token[] | TokensList, callback: (token: Token) => void): Marked;

    static Renderer: typeof marked.Renderer;
    static Lexer: typeof marked.Lexer;
    static Parser: typeof marked.Parser;
    static Tokenizer: typeof marked.Tokenizer;
    static Renderer: typeof marked.Renderer;
    static TextRenderer: typeof marked.TextRenderer;
    static Slugger: typeof marked.Slugger;
  }
}