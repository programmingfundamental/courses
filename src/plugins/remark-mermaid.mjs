/** Preserve fenced Mermaid source while letting the bundled renderer draw it. */
export default function remarkMermaid() {
  return (tree) => {
    const walk = (node) => {
      if (node.type === 'code' && node.lang === 'mermaid') {
        node.type = 'html';
        node.value = `<pre class="mermaid">${node.value.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')}</pre>`;
      }
      node.children?.forEach(walk);
    };
    walk(tree);
  };
}
